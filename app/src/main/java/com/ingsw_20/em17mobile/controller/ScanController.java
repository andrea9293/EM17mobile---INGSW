package com.ingsw_20.em17mobile.controller;

import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * ScanController si occupa della gestione dello scanner
 *
 * @author ingSW20
 */

public class ScanController implements ZXingScannerView.ResultHandler {
    /**
     * Tag utile per il logger
     * @see android.util.Log
     */
    private final String TAG = "ScanControllerTAG";
    /**
     * Variabile per l'utilizzo dello scanner
     */
    private ZXingScannerView mScannerView;

    /**
     * Costruttore chiamato dalla MainActivity al momento della creazione.
     * Si occupa di inizializzare mScannerView
     *
     * @see com.ingsw_20.em17mobile.views.MainActivity
     * @param mScannerView variabile precedentemente inizalizzata in MainActivity
     */
    public ScanController(ZXingScannerView mScannerView){
        // forza scansioni dei soli codici QR
        List<BarcodeFormat> barcodeFormats = new ArrayList<>();
        barcodeFormats.add(BarcodeFormat.QR_CODE);
        mScannerView.setFormats(barcodeFormats);

        this.mScannerView = mScannerView;
    }

    /**
     * handleResult preleva il risultato e lo gestisce. In questo caso invia a DBController.risultatoQuery(String) per
     * la verifica del QR code del biglietto scansionato
     *
     * @see DBController#risultatoQuery(String)
     * @param rawResult risultato della scansione
     */
    @Override
    public void handleResult(Result rawResult) {
        Log.v(TAG, rawResult.getText()); // stampa i risultati della scansione

        // avvia la query da eseguire nel database
        DBController.getInstance().risultatoQuery(rawResult.getText());
    }

    /**
     * Metodo chiamato per riattivare la fotocamera per scansionare nuovamente un codice.
     * Questo metodo viene chiamato alla chiusura del dialog con le informazioni del biglietto
     * @see com.ingsw_20.em17mobile.views.AlertDialogView#scanController
     */
    public void resumeScan(){
        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }

    /**
     * metodo per la gestione dello scanner alla ripresa dell'esecuzione dell'app
     */
    public void onResumeMethod() {
        mScannerView.setResultHandler(this); // registra se stesso come handler per prelevare i risultati della scansione
        mScannerView.startCamera();          // riprende il funzionamento della fotocamera alla ripresa dell'app
    }

    /**
     * metodo per la gestione dello scanner quando l'app è in pausa
     */
    public void onPauseMethod() {
        mScannerView.stopCamera(); // ferma la fotocamera quando l'app va in pausa
    }
}
