package com.example.vanessachia_hui_hsu.numberrecognition;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class MainNumberRecognition extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    static {
        System.loadLibrary("tensorflow_inference");
    }
    private static final String MODEL_FILE = "file:///android_asset/optimized_frozen_mnist_model.pb";
    private static final String INPUT_NODE = "x_input";
    private static final int[] INPUT_SHAPE = {1,784};
    private static final String OUTPUT_NODE = "y_actual";
    private TensorFlowInferenceInterface inferenceInterface;

    private int imageListIndex = 9;
    private int[] imageIDList = {
            R.drawable.digit0,
            R.drawable.digit1,
            R.drawable.digit2,
            R.drawable.digit3,
            R.drawable.digit4,
            R.drawable.digit5,
            R.drawable.digit6,
            R.drawable.digit7,
            R.drawable.digit8,
            R.drawable.digit9
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_number_recognition);

        // Set up the UI elements
        imageView = (ImageView)findViewById(R.id.image_view);
        textView = (TextView)findViewById(R.id.text_view);

        // Initialize the inference variable to use our model
        inferenceInterface = new TensorFlowInferenceInterface();
        inferenceInterface.initializeTensorFlow(getAssets(),MODEL_FILE);
    }

    // Function to call when user presses on predict button
    // Calls the code tp perform the pediction
    public void predictDigitClick(View view){
        // Get the image data as a float array
        float[] pixelBuffer = convertImage();
        // Get the label that represents the prediction
        float[] results = formPrediction(pixelBuffer);
        //  for (float result: results) {
        //  Log.d("result", String.valueOf(result));
        //  }
        printsResults(results);
    }

    private void printsResults(float[] results){
        float max = 0;
        float secondMax = 0;
        int maxIndex = 0;
        int secondMaxIndex = 0;
        for(int i = 0; i < 10; i++) {
            if(results[i] > max){
                secondMax = max;
                secondMaxIndex = maxIndex;
                max = results[i];
                maxIndex = i;
            } else if (results[i] < max && results[i] > secondMax) {
                secondMax = results[i];
                secondMaxIndex = i;
            }
        }
        String output = "Model predicts:" + String.valueOf(maxIndex) +
                ", second choice: " + String.valueOf(secondMaxIndex);
        textView.setText(output);

    }
    // Function to actually make the prediction
    // Takes in array of floats that represents the image data
    // Outputs an array of folats that represents the label based on the current prediction
    private float[] formPrediction(float[] pixelBuffer) {
        // Fill the input node with the pixel buffer
        inferenceInterface.fillNodeFloat(INPUT_NODE, INPUT_SHAPE, pixelBuffer);
        // Make the prediction by running inference on our model and store results in output node
        inferenceInterface.runInference(new String[]{OUTPUT_NODE});
        float[] results = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        // Store value of output node (results) into a float array
        inferenceInterface.readNodeFloat(OUTPUT_NODE,results);
        return  results;
    }

    // Function to convert currently displayed image into a float array to feed into the model
    // Return a float array for input into the model
    private float[] convertImage() {
        // Covert current image to a scaled 28 * 28 bitmap
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),
                imageIDList[imageListIndex]);
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 28, 28, true);
        imageView.setImageBitmap(imageBitmap);
        int[] imageAsIntArray = new int[784];
        float[] imageAsFloatArray = new float[784];
        // Get the pixel values of the bitmap and store them in a flattened int array
        imageBitmap.getPixels(imageAsIntArray, 0, 28, 0, 0, 28, 28);
        // Convert the int array into a float array
        for (int i= 0; i < 784; i++){
            imageAsFloatArray[i] = imageAsIntArray[i] / -16777216;
                    // -16 777 216
        }
        return imageAsFloatArray;
    }

    // Function to call when user presses on load next image button
    // Calls the Code to load and display the next image in the image view
    public void loadNextImageClick(View view){
        if (imageListIndex >= 9){
            imageListIndex = 0;
        }else {
            imageListIndex += 1;
        }
        // imageListIndex = (imageListIndex = >=9)? 0: imageListIndex +1;
        // Load the image found at imagelistIndex from our imageIDList
        imageView.setImageDrawable(getDrawable(imageIDList[imageListIndex]));
    }
}
