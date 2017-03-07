package com.UpTopApps.OilResetPro.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.UpTopApps.OilResetPro.CarsData;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joel on 3/6/2017.
 */

public class DataClass_RP implements Serializable {

    private static DataClass_RP _rpDataClass;
    public List<CarsData> mainArray = new ArrayList<CarsData>();
    public List<CarsData> dataArray = new ArrayList<CarsData>();
    //	private ProgressDialog dialog;
    private String myfavlist = "MYNewNAMEUniQue";
    public boolean showadd = true;
    public boolean intoShowed = false;




    public static synchronized DataClass_RP sharedRPDataClass(){
        if (_rpDataClass == null){
            _rpDataClass = new DataClass_RP();
        }
        return _rpDataClass;
    }

    public boolean back = false;


    public void showWarningWithTitle(Context context , String Title, String Message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(Title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage("" + Message);
        alertDialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) {}});
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public DataClass_RP LoadCurUser(Context context) {
        try {
            FileInputStream fis = context.openFileInput(myfavlist);
            ObjectInputStream is = new ObjectInputStream(fis);
            try {
                _rpDataClass =  (DataClass_RP) is.readObject();
            }
            catch (ClassNotFoundException e) {
//		    Log.e("EXCEPTION", ""+e);
            }
            catch (Exception e) {
                // TODO: handle exception
            }
            is.close();
        }
        catch (StreamCorruptedException e) {

        } catch (IOException e) {

//			    Log.e("EXCEPTION IO", ""+e);
        }
        return _rpDataClass;
    }

    // SAVE USER DETAIL

    public void SaveCurUser(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(myfavlist, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(_rpDataClass);
            os.close();

        }
        catch (FileNotFoundException e) {
//			    Log.e("EXCEPTION", ""+e);
        }
        catch(IOException ioe) {
//			  Log.e("EXCEPTION IO", ""+ioe);

        }
    }
}
