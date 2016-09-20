/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.map;

import javafx.event.Event;

/**
 *
 * @author petranpap
 */
public class MapEvent extends Event {

    public MapEvent(Maps map, double lat, double lng) {
        super(map, Event.NULL_SOURCE_TARGET, Event.ANY);
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLng() {
        return this.lng;
    }

    private double lat;
    private double lng;
}