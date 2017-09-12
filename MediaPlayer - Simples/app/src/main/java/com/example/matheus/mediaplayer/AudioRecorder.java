package com.example.matheus.mediaplayer;


import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;


import java.io.File;
import java.io.IOException;

/**
 * Created by matheus on 22/02/16.
 */
public class AudioRecorder{

    final MediaRecorder recorder = new MediaRecorder();
    final String caminho;

    /**
     * Construtor da classe com o parametro _nome, sendo o nome do arquivo
     */
    public AudioRecorder(String _nome) {
        this.caminho = tratarCaminho(_nome);
    }

    private String tratarCaminho(String _caminho) {
        // Adiciona a barra ao inicio do caminho do arquivo
        if (!_caminho.startsWith("/")) {
            _caminho = "/" + _caminho;
        }
        // Finaliza o caminho com a extensão 3gp
        if (!_caminho.contains(".")) {
            _caminho += ".3gp";
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath() + _caminho;
    }

    /**
     * Inicia a gravação do audio
     */
    public void start() throws IOException {
        String estado = android.os.Environment.getExternalStorageState();
        // Verifica se possui disco SD
        // Caso não possa, lança uma exceção IOException
        if (!estado.equals(android.os.Environment.MEDIA_MOUNTED)) {
            throw new IOException("SD Card nao esta montdao.  Seu estado é " + estado + ".");
        }

        // Verifica se o diretorio não existe e se não pode criar o diretorio
        // Caso não possa, lança uma exceção IOException
        File directory = new File(caminho).getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Caminho do arquivo nao pode ser criado.");
        }
        // Seta a captura do audio pelo microfone
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // Seta o tipo de arquivo de saida, neste caso 3gp
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // Seta o tipo de codec utilizado AMR_NB
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // Seta o arquivo de saida
        recorder.setOutputFile(caminho);
        recorder.prepare();
        recorder.start();
    }

    /**
     * Para a gravação e finaliza
     */
    public void stop() throws IOException {
        recorder.stop();
        recorder.release();
    }

}
