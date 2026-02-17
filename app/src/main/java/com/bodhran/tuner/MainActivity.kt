package com.bodhran.tuner

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bodhran.tuner.databinding.ActivityMainBinding
import kotlin.math.log10
import kotlin.math.pow

class MainActivity : AppCompatActivity(), AudioProcessor.AudioProcessorListener {
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val MIN_SCALE = 1
        private const val MAX_SCALE = 20
        private const val REFERENCE_SCALE = 10
    }
    
    private lateinit var binding: ActivityMainBinding
    private var audioProcessor: AudioProcessor? = null
    private var referenceFrequency: Double? = null
    private var currentFrequency: Double = 0.0
    private var isCapturingReference = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        
        if (checkPermission()) {
            initializeAudioProcessor()
        } else {
            requestPermission()
        }
    }
    
    private fun setupUI() {
        binding.setReferenceButton.setOnClickListener {
            captureReferenceFrequency()
        }
    }
    
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            PERMISSION_REQUEST_CODE
        )
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeAudioProcessor()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun initializeAudioProcessor() {
        audioProcessor = AudioProcessor(this)
        audioProcessor?.startRecording()
        updateStatusIndicator(true)
    }
    
    private fun captureReferenceFrequency() {
        if (currentFrequency > 0) {
            referenceFrequency = currentFrequency
            isCapturingReference = false
            updateReferenceDisplay()
            Toast.makeText(
                this,
                "Reference set to ${currentFrequency.toInt()} Hz",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Start capturing next detected frequency
            isCapturingReference = true
            Toast.makeText(
                this,
                "Play a drum beat to set reference",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    override fun onFrequencyDetected(frequency: Double, amplitude: Double) {
        runOnUiThread {
            currentFrequency = frequency
            
            // If we're in reference capture mode, set the reference
            if (isCapturingReference) {
                referenceFrequency = frequency
                isCapturingReference = false
                updateReferenceDisplay()
                Toast.makeText(
                    this,
                    "Reference set to ${frequency.toInt()} Hz",
                    Toast.LENGTH_SHORT
                ).show()
            }
            
            // Update frequency display
            binding.frequencyValue.text = "${frequency.toInt()} Hz"
            
            // Calculate and update tone reading
            val toneValue = calculateToneValue(frequency)
            binding.toneReading.text = toneValue.toString()
            
            // Flash the display to show detection
            binding.toneReading.setTextColor(getColorForTone(toneValue))
        }
    }
    
    override fun onError(error: String) {
        runOnUiThread {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            updateStatusIndicator(false)
        }
    }
    
    private fun calculateToneValue(frequency: Double): Int {
        val refFreq = referenceFrequency
        
        if (refFreq == null) {
            // If no reference is set, use a simple linear mapping
            // Map 60-300 Hz to 1-20 scale
            val normalized = (frequency - 60.0) / (300.0 - 60.0)
            val value = (normalized * (MAX_SCALE - MIN_SCALE) + MIN_SCALE).toInt()
            return value.coerceIn(MIN_SCALE, MAX_SCALE)
        } else {
            // Use logarithmic scale relative to reference
            // This provides a more musically accurate representation
            
            // Calculate semitone difference from reference
            val semitones = 12 * log10(frequency / refFreq) / log10(2.0)
            
            // Map semitones to scale
            // Assuming approximately ±12 semitones (one octave) maps to the full scale
            val scalePerSemitone = (MAX_SCALE - MIN_SCALE).toDouble() / 24.0 // ±12 semitones
            val value = (REFERENCE_SCALE + semitones * scalePerSemitone).toInt()
            
            return value.coerceIn(MIN_SCALE, MAX_SCALE)
        }
    }
    
    private fun getColorForTone(tone: Int): Int {
        return when {
            tone < REFERENCE_SCALE -> Color.parseColor("#FF3700B3") // Purple (below reference)
            tone == REFERENCE_SCALE -> Color.parseColor("#FF4CAF50") // Green (at reference)
            else -> Color.parseColor("#FF6200EE") // Lighter purple (above reference)
        }
    }
    
    private fun updateStatusIndicator(isListening: Boolean) {
        if (isListening) {
            binding.statusIndicator.text = getString(R.string.listening)
            binding.statusIndicator.setTextColor(ContextCompat.getColor(this, R.color.green))
        } else {
            binding.statusIndicator.text = getString(R.string.not_listening)
            binding.statusIndicator.setTextColor(ContextCompat.getColor(this, R.color.gray))
        }
    }
    
    private fun updateReferenceDisplay() {
        val refFreq = referenceFrequency
        if (refFreq != null) {
            binding.referenceValue.text = "${refFreq.toInt()} Hz"
        } else {
            binding.referenceValue.text = getString(R.string.reference_value)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        audioProcessor?.stopRecording()
    }
    
    override fun onPause() {
        super.onPause()
        audioProcessor?.stopRecording()
        updateStatusIndicator(false)
    }
    
    override fun onResume() {
        super.onResume()
        if (checkPermission() && audioProcessor != null) {
            audioProcessor?.startRecording()
            updateStatusIndicator(true)
        }
    }
}
