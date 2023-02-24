package com.toren.speechtotexttutorial

import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.toren.speechtotexttutorial.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isAudioPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            isAudioPermissionGranted = permissions[RECORD_AUDIO] ?: isAudioPermissionGranted
        }
        requestPermission()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault())

        speechRecognizer!!.setRecognitionListener(object: RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {}

            override fun onBeginningOfSpeech() {
                binding.textInput.editText!!.setText("listening...")
                binding.mic.setImageResource(R.drawable.ic_mic_green)
            }

            override fun onRmsChanged(p0: Float) {}

            override fun onBufferReceived(p0: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(p0: Int) {}

            override fun onResults(bundle: Bundle?) {

                val data = bundle!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                println(data!![0])

                if (data[0].isNotBlank() || data[0].isNotEmpty()) {
                    binding.mic.setImageResource(R.drawable.ic_mic)
                    binding.textInput.editText!!.setText(data!![0])
                }
            }
            override fun onPartialResults(p0: Bundle?) {}

            override fun onEvent(p0: Int, p1: Bundle?) {}

        })

        binding.apply {
            mic.setOnClickListener {
                //checkPermission(Manifest.permission.RECORD_AUDIO,AUDIO_PERMISSION_CODE);
                if (requestPermission()) {
                    speechRecognizer!!.startListening(speechRecognizerIntent)
                } else {

                }
            }
        }
    }

    private fun requestPermission() : Boolean {
        isAudioPermissionGranted = ContextCompat.checkSelfPermission(
            this,RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

        val permissionRequest : MutableList<String> = ArrayList()

        if (!isAudioPermissionGranted) {
            permissionRequest.add(RECORD_AUDIO)
        }
        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
        isAudioPermissionGranted = ContextCompat.checkSelfPermission(
            this,RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

        return isAudioPermissionGranted
    }
}