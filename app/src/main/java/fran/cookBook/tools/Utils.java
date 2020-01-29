package fran.cookBook.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

//Clase con algunas herramientas reutilizables
public class Utils {

    public static class basic {

        //retorna la rotación del dispositivo
        public static String getRotation(Context context){
            final int angle = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
            switch (angle) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_180:
                    return "v";
                case Surface.ROTATION_90:
                default:
                    return "h";
            }
        }

        //retorna una lista a partir de un texto que contiene elementos separados por {}
        public static ArrayList<String> getList(String text){

            ArrayList<String>list=new ArrayList<>();

            if(text!=null){

                int first =0;
                int last =0;

                for(int i=0;i<text.length();i++){

                    if(text.charAt(i)=='{'){
                        first =i+1;
                    }else if(text.charAt(i)=='}'){
                        last =i;
                        String palabra= text.substring(first, last);
                        list.add(palabra);
                    }
                }

                return list;

            }else{

                return null;
            }

        }

        //retorna una String con elementos separados por {} a partir de una lista
        public static String getStringfromList(ArrayList<String> list){

            String text="";

                for(String a:list){
                    text = text + "{" + a  + "}";
                }

          if(text.equals("{}")){
              return "";
          }else{
              Log.e("text",text);
              return text;
          }

        }

        //retorna una String con elementos separados por {} a partir de una array
        public static String getStringfromArray(String[] arr){

            String text="";

            for(String a:arr){
                text = text + "{" + a  + "}";
            }

            if(text.equals("{}")){
                return "";
            }else{
                Log.e("text",text);
                return text;
            }

        }


        //añade un view a un viewgroup
        public static void addItem(int viewId, ViewGroup layout,Context con){

            LayoutInflater inflater = LayoutInflater.from(con);
            final View rowView = (LinearLayout) inflater.inflate(viewId, null, false);
            layout.addView(rowView, layout.getChildCount() );


        }


    }

    //herramientas específicas para la imagen
    public static class photo {

        //cambia la resolución de la imagen
        public static Bitmap resizeImage(Bitmap bm){

            int w = bm.getWidth();
            int h = bm.getHeight();

            Log.e("w",String.valueOf(w));
            Log.e("h",String.valueOf(h));

            int scaleW=0;
            int scaleH=0;

            if(w>h){
                scaleW = 1600;
                scaleH = h * 1600 / w ;
            }else if(h>w){
                scaleH = 1600;
                scaleW = w * 1600 / h ;
            }else{
                scaleW = 1600;
                scaleH = 1600;
            }


            // "RECREATE" THE NEW BITMAP
            Bitmap reSizedBitmap = Bitmap.createScaledBitmap(bm, scaleW, scaleH, false);

            Log.e("scaleW",String.valueOf(scaleW));
            Log.e("scaleH",String.valueOf(scaleH));

            return reSizedBitmap;


        }

        //rota la imagen
        public static Bitmap rotateImage(Bitmap bmp, Uri uri, Context con) throws IOException {

            InputStream input = null;
            input = con.getContentResolver().openInputStream(uri);
            ExifInterface ei;
            if (Build.VERSION.SDK_INT > 23) {
                ei = new ExifInterface(input);
            } else{
                ei = new ExifInterface(uri.getPath());
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Bitmap RotatedBitmap;
            Matrix matrix = new Matrix();

            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    RotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                            matrix, true);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(100);
                    RotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                            matrix, true);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    RotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
                            matrix, true);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    RotatedBitmap = bmp;
            }
            return RotatedBitmap;
        }

        //Transforma un bitmap en un byte[]
        public static byte[] bitmapToBlob(Bitmap bitmap){

            ByteArrayOutputStream baos = new ByteArrayOutputStream(20480);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , baos);
            byte[] blob = baos.toByteArray();

            return blob;
        }

        //Transforma un byte[] en un bitmap
        public static Bitmap blobToBitmap(byte[]blob){

            ByteArrayInputStream bais = new ByteArrayInputStream(blob);
            Bitmap bitmap = BitmapFactory.decodeStream(bais);

           return bitmap;

        }


    }


}
