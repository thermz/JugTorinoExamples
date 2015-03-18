/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.jug.jugtorinoexaples.sampledsl;

public class Recorder<T> {

    private T t;
    private RecordingObject recorder;

    public Recorder(T t, RecordingObject recorder) {
        this.t = t;
        this.recorder = recorder;
    }

    public String getCurrentPropertyName() {
        return recorder.getCurrentPropertyName();
    }

    public T getObject() {
        return t;
    }
}