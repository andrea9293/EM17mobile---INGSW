package com.ingsw_20.em17mobile.views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ingsw_20.em17mobile.R;
import com.ingsw_20.em17mobile.controller.AllViewController;
import com.ingsw_20.em17mobile.controller.DBController;
import com.ingsw_20.em17mobile.controller.ScanController;
import com.ingsw_20.em17mobile.model.TicketModel;

import java.util.Observable;
import java.util.Observer;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Questa è la Activity principale contenente la schermata per la scansione del codice QR.
 * MainActivity implementa l'interfaccia Observer utile per il design pattern MVC
 *
 * @author ingSW20
 * @see java.util.Observer
 */
public class MainActivity extends AppCompatActivity implements Observer {
    /**
     * Tag utile per il logger
     *
     * @see android.util.Log
     */
    private String TAG = "MainActivityTAG";

	/**
     * variabile per l'inizializzazione della view responsabile dei popup
     */
    private AlertDialogView alertDialogView;

    /**
     * Questa TextView contiene le informazioni sul biglietto e viene
     * aggiornata tramite il metodo update
     *
     * @see #update(Observable, Object)
     */
    private TextView resulText;
    
	/**
     * Variabile per la gestione della scansione dei codici
     */
    private ScanController scanController;
    
	/**
     * ImageView che mostra se il biglietto scansioniato è valido o meno.
     * Viene aggiornata tramite il metodo update
     *
     * @see #update(Observable, Object)
     */
    private ImageView imageView;
    
	/**
     * Variabile utilizzata dal metodo onBackPressed() per evitare la chiusura
     * accidentale dell'applicazione
     */
    private long backPressedTime = 0;
    
	/**
     * Variabile per la richiesta del permesso alla fotocamera
     */
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    /**
     * metodo autogenerato per la creazione ed inizializzazione della view
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //richiesta del permesso per la fotocamera
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }

        resulText = findViewById(R.id.resultText);
        imageView = findViewById(R.id.imageView);

        imageView.setVisibility(View.INVISIBLE);


        // scanLayout consiste nel riquadro centrale dove verrà poi posto lo scanner,
        // mentre mScannerView è la view che contiene scanner effettivo che
        // verrà caricato all'interno di scanLayout.
        //
        // Successivamente viene inizializzato scanController, passando mScannerView come parametro,
        // che si occuperà della gestione dello scanner
        ConstraintLayout scanLayout = findViewById(R.id.scanLayout);
        ZXingScannerView mScannerView = new ZXingScannerView(this);
        scanLayout.addView(mScannerView);
        scanController = new ScanController(mScannerView);

        // inizializzo la classe della view responsabile dei popup
        // passandogli anche il fragmentManager utile per la visualizzazione del popup
        alertDialogView = new AlertDialogView(scanController);
        Log.d(TAG, "assegno il fragment");
        AlertDialogView.fragmentManager = getSupportFragmentManager();
        Log.d(TAG, "fragment assegnato");

        //registro l'Observer nel model
        TicketModel.getInstance().addObserver(this);
    }

    /**
     * Metodo autogenerato per la creazione del menu
     *
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Questo metodo controlla la pressione del tasto indietro al fine di evitare
     * terminazioni accidentali dell'applicazione
     *
     * @see AppCompatActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secondi
            backPressedTime = t;
            Toast.makeText(this, "Premere di nuovo per uscire dall'app", Toast.LENGTH_LONG).show();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Metodo autogenerato per la gestione della pressione dei tasti del menu.
     * Nel caso in ui venga premuto sul tasto Logout, questo chiamerà il metodo utile per il logout
     *
     * @param item
     * @return
     * @see DBController#logOut()
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            alertDialogView.onDestroy(); // distrugge la view responsabile dei popup
            DBController.getInstance().logOut(); // esegue il logout todo sta cosa qua non sta bene
            AllViewController.getInstance().toLoginActivity(MainActivity.this); // va alla mainActivity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Metodo autogenerato che gestisce la ripresa dell'esecuzione dell'applicazione
     */
    @Override
    public void onResume() {
        AlertDialogView.fragmentManager = getSupportFragmentManager();
        scanController.onResumeMethod(); // riprende la funzionalità di scannner
        super.onResume();

    }

    /**
     * Metodo autogenerato per la gestione della messa in pausa dell'applicazione
     */
    @Override
    public void onPause() {
        scanController.onPauseMethod(); // mette in paus lo scanner
        super.onPause();
    }

    /**
     * Metodo richiesto dall'interfaccia Observer. Si occupa dell'aggiornamento dei componenti della view
     * prelevando i dati direttamente da TicketModel
     *
     * @param o
     * @param arg
     * @see java.util.Observer
     * @see com.ingsw_20.em17mobile.model.TicketModel
     */
    @Override
    public void update(Observable o, Object arg) {
        imageView.setVisibility(View.VISIBLE);
        if (TicketModel.getInstance().getValidated()) {
            String event = "Evento: " + TicketModel.getInstance().getEvent() + "\n";
            String ticketHolder = "Biglietto intestao a " + TicketModel.getInstance().getName() + " " + TicketModel.getInstance().getSurname() + "\n";
            String accessesNumber = "Numero di accessi: " + TicketModel.getInstance().getAccessesNumber();

            String ticket = event + ticketHolder + accessesNumber;
            resulText.setText(ticket);
            imageView.setImageDrawable(this.getResources().getDrawable(R.drawable.ok_96px));

        } else {
            resulText.setText(R.string.biglietto_non_valido);
            imageView.setImageDrawable(this.getResources().getDrawable(R.drawable.cancel_96px));
        }
    }

    /**
     * questo metodo si occupa di stampare su schermo un messaggio di tipo Toast
     *
     * @param message messaggio da stampare
     */
    private void printToastMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Questo metodo viene chiamato quando si verifica la concessione dei permessi android
     *
     * @param requestCode  codice identificativo del permesse
     * @param permissions
     * @param grantResults risultato della richiesta dei permessi
     * @see AppCompatActivity#onRequestPermissionsResult(int, String[], int[])
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permessi concessi
                    Log.d(TAG, "permessi concessi correttamente");

                } else {
                    //permessi negati
                    printToastMessage("l'app necessita dei permessi per funzioinare correttamente");
                }
            }
        }
    }
}
