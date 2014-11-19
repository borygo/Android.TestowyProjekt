package android.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends Activity implements View.OnClickListener, View.OnLongClickListener
{
    Button btPrzycisk;
    TextView tvLabelka;
    EditText etTextBox;
    CheckBox cbZaznaczenie;
    ImageButton imgBtStar;
    ImageButton imgBtSilent;
    ImageButton imgBtZakoncz;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        btPrzycisk = (Button)findViewById(R.id.btPrzycisk);
        tvLabelka = (TextView)findViewById(R.id.tvLabelka);
        tvLabelka.setText(getString(R.string.labeltext));
        etTextBox = (EditText)findViewById(R.id.etTextBox);
        cbZaznaczenie = (CheckBox)findViewById(R.id.cbZaznaczenie);
        imgBtStar = (ImageButton)findViewById(R.id.przyciskObrazkowy1);
        imgBtSilent = (ImageButton)findViewById(R.id.przyciskObrazkowy2);
        imgBtZakoncz = (ImageButton)findViewById(R.id.btZakoncz);
        
        imgBtZakoncz.setOnClickListener(this);
        imgBtZakoncz.setOnLongClickListener(this);
        
        imgBtStar.setOnClickListener(this);
        imgBtSilent.setOnClickListener(this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.option1:
                tvLabelka.setVisibility(View.VISIBLE);
                break;
            case R.id.option2:
                tvLabelka.setVisibility(View.INVISIBLE);
                break;
            case R.id.option3:
                btPrzycisk.setVisibility(btPrzycisk.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                break;
        }
        return true;
    }
    
    public void obslugaKlikniecia(View view) {
        String tekst = getResources().getString(R.string.wiadomosc);
        if (cbZaznaczenie.isChecked())
            tekst = etTextBox.getText().toString();
        try {
            Socket client = new Socket("192.168.1.6", 5000);
            OutputStream outStream = client.getOutputStream();
            InputStream inStream = client.getInputStream();
            try {
                byte[] bytesToSend = tekst.getBytes();
                byte[] buffer = new byte[1024];
                outStream.write(bytesToSend);
                int bytesRead = inStream.read(buffer, 0, buffer.length);
                String response = new String(buffer).substring(0, bytesRead);
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            } catch(IOException ex){
                PrintException(ex);
            } finally {
                outStream.close();
                outStream.flush();
                client.close();
            }
            
        } catch (IOException ex) {
            PrintException(ex);
            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void PrintMessage(String message) {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setMessage(message);
        alert.setCancelable(false);
        alert.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }
    
    private void PrintException(Exception ex){
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setMessage(ex.toString());
        alert.setCancelable(false);
        alert.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public boolean onLongClick(View v) {
        if (v == imgBtZakoncz) {
            Toast.makeText(this, "dupa", RESULT_OK).show();
            return true;
        }
        return false;
    }

    public void onClick(View v) {
        String sdDir = Environment.getExternalStorageDirectory().getPath();            
        String myAppDir = "myapp";
        String dirPath = String.format("%s/%s", sdDir, myAppDir);
        String filePath = String.format("%s/%s/%s", sdDir, myAppDir, "myfirstfile.dat");
        File newDir = new File(dirPath);
        File newFile = new File(filePath);
        if (v == imgBtZakoncz){
            finish();
        } else if (v == imgBtStar) {
            FileOutputStream outStream = null;
            OutputStreamWriter writer = null;
            try {
                if (!newDir.exists())
                    newDir.mkdirs();
                if (!newFile.exists() && newFile.createNewFile())
                    PrintMessage("udalo sie utworzyc plik");
                outStream = new FileOutputStream(newFile);
                writer = new OutputStreamWriter(outStream);
                writer.write(etTextBox.getText().toString());
            } catch (FileNotFoundException fileNotFoundEx) {
                PrintException(fileNotFoundEx);
            } catch (IOException ioEx) {
                PrintException(ioEx);
            } finally {
                try {
                    if (writer != null)
                        writer.close();
                    if (outStream != null)
                        outStream.close();
                } catch (IOException ex) {
                    PrintException(ex);
                }
            }
            
        } else if (v == imgBtSilent) {
            FileInputStream inStream = null;
            InputStreamReader reader = null;
            BufferedReader bufReader = null;
            try {
                if (newFile.exists()) {
                    inStream = new FileInputStream(newFile);
                    reader = new InputStreamReader(inStream);
                    bufReader = new BufferedReader(reader);
                    String fileContent = bufReader.readLine();
                    if (!TextUtils.isEmpty(fileContent)) {
                        PrintMessage(fileContent);
                    }
                    newFile.delete();
                    newDir.delete();
                } else
                    PrintMessage("nie znaleziono pliku");
            } catch (FileNotFoundException fNotFoundEx) {
                PrintException(fNotFoundEx);
            } catch (IOException ioEx) {
                PrintException(ioEx);
            } finally {
                try {
                    if (bufReader != null)
                        bufReader.close();
                    if (reader != null)
                        reader.close();
                    if (inStream != null)
                        inStream.close();
                } catch(IOException ioEx) {
                    PrintException(ioEx);
                }
            }
            
        }
    }
}
