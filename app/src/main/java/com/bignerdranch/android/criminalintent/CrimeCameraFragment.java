package com.bignerdranch.android.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * Created by alexander on 8/20/15.
 */

@SuppressWarnings("deprecation")
public class CrimeCameraFragment extends Fragment {
    public static final String EXTRA_PHOTO_FILENAME =
            "com.bignerdranch.android.criminalintent.photo_filename";
    private static final String LOG_TAG_CAMERA = "CrimeCameraFragment";
    private Camera camera;
    private SurfaceView surfaceView;
    private View progressContainer;


    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            progressContainer.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String filename = UUID.randomUUID().toString() + ".jpg";
            FileOutputStream fout = null;
            boolean success = true;

            try {
                fout = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                fout.write(data);
            } catch (Exception e) {
                Log.e(LOG_TAG_CAMERA, "Error writing to file " + filename, e);
                success = false;
            } finally {
                try {
                    if (fout != null)
                        fout.close();
                } catch (Exception e) {
                    Log.e(LOG_TAG_CAMERA, "Error closing file " + filename, e);
                    success = false;
                }
            }

            if (success) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_PHOTO_FILENAME, filename);
                getActivity().setResult(Activity.RESULT_OK, intent);
            } else {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }

            getActivity().finish();
        }
    };


    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_camera, parent, false);

        progressContainer = view.findViewById(R.id.crime_camera_progressContainer);
        progressContainer.setVisibility(View.INVISIBLE);

        Button takePictureButton = (Button) view.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (camera != null) {
                    camera.takePicture(shutterCallback, null, pictureCallback);
                }
            }
        });

        surfaceView = (SurfaceView) view.findViewById(R.id.crime_camera_surfaceView);
        final SurfaceHolder holder = surfaceView.getHolder();

        // method setType() and constant are deprecated but is needed
        // for live view before sdk 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    if (camera != null) {
                        camera.setPreviewDisplay(holder);
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG_CAMERA, "Error setting up preview display", e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int w, int h) {
                if (camera == null)
                    return;

                Camera.Parameters parameters = camera.getParameters();

                Size size = getBestSupportedSize(parameters.getSupportedPreviewSizes());
                parameters.setPreviewSize(size.width, size.height);

                size = getBestSupportedSize(parameters.getSupportedPictureSizes());
                parameters.setPictureSize(size.width, size.height);

                camera.setParameters(parameters);

                try {
                    camera.startPreview();
                } catch (Exception e) {
                    Log.e(LOG_TAG_CAMERA, "Could not start preview", e);
                    camera.release();
                    camera = null;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if (camera != null) {
                    camera.stopPreview();
                }
            }
        });

        return view;
    }

    @TargetApi(9)
    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            camera = Camera.open(0);
        } else {
            camera = Camera.open();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    /**
     * A simple algorithm to get the largest size available.
     * For a more robust version, see CameraPreview.java
     * in the ApiDemos sample app from Android.
     *
     * @param sizes List of the camera’s allowable preview sizes
     * @return The largest live preview size available
     */
    private Size getBestSupportedSize(List<Size> sizes) {
        Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Size size : sizes) {
            int area = size.width * size.height;
            if (area > largestArea) {
                bestSize = size;
                largestArea = area;
            }
        }
        return bestSize;
    }
}
