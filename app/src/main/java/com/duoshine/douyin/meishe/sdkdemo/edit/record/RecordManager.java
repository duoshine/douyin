package com.duoshine.douyin.meishe.sdkdemo.edit.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by ms on 2018/8/9.
 */

public class RecordManager {

    private static RecordManager mInstance;
    String mDirName;
    private MediaRecorder mRecorder;
    /*
    * 音频获取源
    * Audio acquisition source
    * */
    public static int audioSource = MediaRecorder.AudioSource.MIC;
    /*
    * 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    * Set the audio sampling rate, 44100 is the current standard, but some devices still support 22050, 16000, 11025
    * */
    public static int sampleRateInHz = 44100;
    /*
    * 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    * Set the audio recording channel CHANNEL_IN_STEREO to two-channel and CHANNEL_CONFIGURATION_MONO to mono
    * */
    public static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    /*
    * 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    * Audio data format: PCM 16 bits per sample. Guaranteed device support.
    *  PCM 8 bits per sample. Not necessarily supported by the device.
    * */
    public static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    /*
    * 缓冲区字节大小
    * Buffer byte size
    * */
    public static int bufferSizeInBytes = 0;
    OnRecordStartListener onRecordStart;

    public interface OnRecordStartListener {
        void onRecordStart(Long id, String filePath);
        void onRecordEnd();
    }

    public void setOnRecordStart(OnRecordStartListener onRecordStart) {
        this.onRecordStart = onRecordStart;
    }

    public static RecordManager getInstance() {
        if (mInstance == null) {
            synchronized (RecordManager.class) {
                if (mInstance == null) {
                    mInstance = new RecordManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 资源释放
     * Release of resources
     */
    public void release() {
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                onRecordStart.onRecordEnd();
                mRecorder.release();
                mRecorder = null;
            }else {
                onRecordStart.onRecordEnd();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否有录音权限
     * Determine if you have recording rights
     * @return
     */
    public static boolean isHasPermission() {
        bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        try {
            audioRecord.startRecording();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        Log.i("录音权限", "录音权限" + audioRecord.getRecordingState());
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            return false;
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
        return true;
    }

    /**
     * 准备开始录制
     * Ready to start recording
     */
    File file;
    public void prepareAudio(String dirName) {
        try {
            File dir = new File(dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            long nowTime=System.currentTimeMillis();
            String fileNameString = nowTime + ".mp3";
            file = new File(dir, fileNameString);
            if (!file.exists()) {
                file.createNewFile();
            }
            mDirName=file.getAbsolutePath();
            mRecorder = new MediaRecorder();
            /*
            * 设置输出文件
            * Set the output file
            * */
            mRecorder.setOutputFile(file.getAbsolutePath());
            /*
            * 设置meidaRecorder的音频源是麦克风
            * Set meidaRecorder's audio source to be a microphone
            * */
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            /*
            * 设置文件音频的输出格式为MPEG_4
            * Set the file audio output format to MPEG_4
            * */
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            /*
            * 设置音频的编码格式为AAC
            * Set the audio encoding format to AAC
            * */
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.prepare();
            mRecorder.start();

            onRecordStart.onRecordStart(nowTime,mDirName);
        } catch (IllegalStateException e) {
            if (mRecorder != null) {
                mRecorder.release();
                mRecorder = null;
            }
            if (file.exists()){
                file.delete();
            }
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

