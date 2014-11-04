package android.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends Activity
{
    Button btPrzycisk;
    TextView tvLabelka;
    EditText etTextBox;
    CheckBox cbZaznaczenie;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btPrzycisk = (Button)findViewById(R.id.btPrzycisk);
        tvLabelka = (TextView)findViewById(R.id.tvLabelka);
        etTextBox = (EditText)findViewById(R.id.etTextBox);
        cbZaznaczenie = (CheckBox)findViewById(R.id.cbZaznaczenie);
        tvLabelka.setText(getString(R.string.labeltext));
        tvLabelka.setTextColor(Color.GREEN);
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
    
    
}
