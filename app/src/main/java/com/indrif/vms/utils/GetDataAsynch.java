/*
package com.indrif.vms.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.util.List;

public class GetDataAsynch extends AsyncTask<Void, Void, Bitmap> {


    private GetDataAsynchListener mListener;

    public GetDataAsynch() {

        if(mListener != null){

            mListener.onBackground();
        }
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        if(mListener != null){

            List list = mListener.onBackground();

            return list;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap t) {
        if(mListener != null){

            if ( t != null) {
                mListener.onPostExecute(t);
            }
        }

    }


    public void setListener(GetDataAsynchListener mListener){

        this.mListener = mListener;
    }

    public interface GetDataAsynchListener {

        public List onBackground();

        public void onPostExecute(Bitmap list);

    }
}*/
