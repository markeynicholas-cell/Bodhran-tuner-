package com.bodhran.tuner

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import org.jtransforms.fft.DoubleFFT_1D
import kotlin.math.abs
import kotlin.math.sqrt

class AudioProcessor(private val listener: AudioProcessorListener) {
    
    interface AudioProcessorListener {
        fun onFrequencyDetected(frequency: Double, amplitude: Double)
        fun onError(error: String)
    }
    
    companion object {
        private const val TAG = "AudioProcessor"
        private const val SAMPLE_RATE = 44100
        private const val BUFFER_SIZE_MULTIPLIER = 2
        private const val MIN_FREQUENCY = 60.0
        private const val MAX_FREQUENCY = 300.0
        
        // Amplitude threshold for detecting drum hits
        private const val AMPLITUDE_THRESHOLD = 0.1
    }
    
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var recordingThread: Thread? = null
    
    private val bufferSize: Int = AudioRecord.getMinBufferSize(
        SAMPLE_RATE,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    ) * BUFFER_SIZE_MULTIPLIER
    
    fun startRecording() {
        if (isRecording) return
        
        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
            
            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                listener.onError("Failed to initialize AudioRecord")
                return
            }
            
            audioRecord?.startRecording()
            isRecording = true
            
            recordingThread = Thread { processAudio() }
            recordingThread?.start()
            
        } catch (e: SecurityException) {
            listener.onError("Microphone permission not granted")
            Log.e(TAG, "SecurityException: ${e.message}")
        } catch (e: Exception) {
            listener.onError("Error starting audio recording")
            Log.e(TAG, "Exception: ${e.message}")
        }
    }
    
    fun stopRecording() {
        isRecording = false
        
        try {
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
            
            recordingThread?.join()
            recordingThread = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping recording: ${e.message}")
        }
    }
    
    private fun processAudio() {
        val audioBuffer = ShortArray(bufferSize)
        
        while (isRecording) {
            val readSize = audioRecord?.read(audioBuffer, 0, bufferSize) ?: 0
            
            if (readSize > 0) {
                // Convert to double array for FFT
                val doubleBuffer = DoubleArray(readSize * 2) // Real and imaginary parts
                for (i in 0 until readSize) {
                    doubleBuffer[i * 2] = audioBuffer[i].toDouble() / Short.MAX_VALUE
                    doubleBuffer[i * 2 + 1] = 0.0 // Imaginary part
                }
                
                // Calculate amplitude (RMS)
                val amplitude = calculateRMS(audioBuffer, readSize)
                
                // Only process if amplitude is above threshold (drum hit detected)
                if (amplitude > AMPLITUDE_THRESHOLD) {
                    // Perform FFT
                    val fft = DoubleFFT_1D(readSize.toLong())
                    fft.complexForward(doubleBuffer)
                    
                    // Find dominant frequency
                    val frequency = findDominantFrequency(doubleBuffer, readSize)
                    
                    // Only report frequencies in our range
                    if (frequency in MIN_FREQUENCY..MAX_FREQUENCY) {
                        listener.onFrequencyDetected(frequency, amplitude)
                    }
                }
            }
            
            // Small delay to avoid excessive CPU usage
            Thread.sleep(50)
        }
    }
    
    private fun calculateRMS(buffer: ShortArray, size: Int): Double {
        var sum = 0.0
        for (i in 0 until size) {
            val normalized = buffer[i].toDouble() / Short.MAX_VALUE
            sum += normalized * normalized
        }
        return sqrt(sum / size)
    }
    
    private fun findDominantFrequency(fftData: DoubleArray, dataSize: Int): Double {
        val magnitudes = DoubleArray(dataSize / 2)
        
        // Calculate magnitudes from complex FFT output
        for (i in 0 until dataSize / 2) {
            val real = fftData[i * 2]
            val imag = fftData[i * 2 + 1]
            magnitudes[i] = sqrt(real * real + imag * imag)
        }
        
        // Find the bin with maximum magnitude in our frequency range
        val minBin = (MIN_FREQUENCY * dataSize / SAMPLE_RATE).toInt()
        val maxBin = (MAX_FREQUENCY * dataSize / SAMPLE_RATE).toInt().coerceAtMost(magnitudes.size - 1)
        
        var maxMagnitude = 0.0
        var maxBin = minBin
        
        for (i in minBin..maxBin) {
            if (magnitudes[i] > maxMagnitude) {
                maxMagnitude = magnitudes[i]
                maxBin = i
            }
        }
        
        // Convert bin to frequency
        return maxBin.toDouble() * SAMPLE_RATE / dataSize
    }
}
