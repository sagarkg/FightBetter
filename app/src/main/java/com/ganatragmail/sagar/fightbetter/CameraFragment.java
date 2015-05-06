package com.ganatragmail.sagar.fightbetter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sagar on 1/14/2015.
 */
public class CameraFragment extends Fragment {
    private static final String TAG ="CameraFragment";

    public static final String EXTRA_PHOTO_FILENAME = "com.ganatragmail.sagar.fightbetter.photo_filename";



    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_camera, parent, false);

        mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        final Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback(){

            @Override
            public void onShutter() {
                //Display progress indicator
                mProgressContainer.setVisibility(View.VISIBLE);
            }
        };

        final Camera.PictureCallback mJPEGCallback = new Camera.PictureCallback() {


            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //Create filename

                String filename = UUID.randomUUID().toString()+ ".jpg";

                //Save jpeg to disk

                FileOutputStream os = null;
                boolean success = true;

                try{
                    os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                    os.write(data);
                }catch(Exception e){
                    Log.e(TAG, "Error writing to file", e);
                    success = false;
                }finally {
                    try{if(os != null)
                        os.close();

                    }catch (Exception e){
                        Log.e(TAG, "Error closing file", e);
                        success = false;
                    }
                }

                if (success) {
                    Log.i(TAG, "JPEG saved at: " + filename);
                    Intent i = new Intent();
                    i.putExtra(EXTRA_PHOTO_FILENAME, filename);
                    getActivity().setResult(Activity.RESULT_OK, i);

                }else {
                    getActivity().setResult(Activity.RESULT_CANCELED);
                }

                getActivity().finish();
            }
        };


        final Button takePicture = (Button)v.findViewById(R.id.crime_camera_takePictureButton);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera!=null){

                    mCamera.takePicture(mShutterCallback, null, mJPEGCallback);

                }

            }
        });


        SurfaceView mSurfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);

        SurfaceHolder holder = mSurfaceView.getHolder();
        //setType()and Surface_Type_Push_buffers are deprecated but required for Camera on pre 3.0 devices
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //Tell camera to use surface as it's preview area
                try{
                    if (mCamera != null)
                        mCamera.setPreviewDisplay(holder);

                }catch(IOException e){
                    Log.e(TAG, "Error setting up preview display", e);
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mCamera == null)
                    return;

                //The surface has changed size; update camera preview size
                Camera.Parameters parameters= mCamera.getParameters();
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
                parameters.setPreviewSize(s.width, s.height);
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
                parameters.setPictureSize(s.width, s.height);
                mCamera.setParameters(parameters);

                try{
                    mCamera.startPreview();

                } catch (Exception e) {
                    Log.e(TAG, "Could not start preview", e);
                    mCamera.release();
                    mCamera=null;
                }



            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // We no longer display in the surface so stop preview
                if (mCamera != null)
                    mCamera.stopPreview();
            }
        });

        return v;

    }

    @TargetApi(9)
    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
            mCamera= Camera.open(0);
        else
            mCamera= Camera.open();

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mCamera!=null){
            mCamera.release();
            mCamera = null;
        }

    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }

}
