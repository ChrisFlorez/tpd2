package tdp2.model;

import android.location.Location;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import static java.lang.Boolean.FALSE;

public class BankATM {
    private int id = 0;
    @SerializedName(value = "long") private double longitude = 0;
    private double lat = 0;
    private String banco = "";
    private String red = "";
    private String ubicacion = "";
    private String localidad = "";
    private int terminales = 0;
    private boolean no_vidente = FALSE;
    private boolean dolares = FALSE;
    private String calle = "";
    private int altura = 0;
    private String calle2 = "";
    private String barrio = "";
    private String comuna = "";
    private String codigo_postal = "";
    private String codigo_postal_argentino = "";
    private Location location;


    public void setId(int id) {
        this.id = id;
    }

    private void setLong(double longitude) {
        this.longitude = longitude;
    }

    private void setLat(double lat) {
        this.lat = lat;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public void setRed(String red) {
        this.red = red;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public void setTerminales(int terminales) {
        this.terminales = terminales;
    }

    public void setNoVidente(boolean no_vidente) {
        this.no_vidente = no_vidente;
    }

    public void setDolares(boolean dolares) {
        this.dolares = dolares;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public void setCalle2(String calle2) {
        this.calle2 = calle2;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public void setCodigoPostal(String codigo_postal) {
        this.codigo_postal = codigo_postal;
    }

    public void setCodigoPostalArgentino(String codigo_postal_argentino) {
        this.codigo_postal_argentino = codigo_postal_argentino;
    }

    public Location getLocation() {
        this.location = new Location("JSON");
        this.location.setLatitude(lat);
        this.location.setLongitude(longitude);

        return location;
    }

    public int getId() {
        return id;
    }

    public double getLong() {
        return longitude;
    }

    public double getLat() {
        return lat;
    }

    public String getBanco() {
        return banco;
    }

    public String getRed() {
        return red;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public int getTerminales() {
        return terminales;
    }

    public boolean isNoVidente() {
        return no_vidente;
    }

    public boolean isDolares() {
        return dolares;
    }

    public String getCalle() {
        return calle;
    }

    public int getAltura() {
        return altura;
    }

    public String getCalle2() {
        return calle2;
    }

    public String getBarrio() {
        return barrio;
    }

    public String getComuna() {
        return comuna;
    }

    public String getCodigoPostal() {
        return codigo_postal;
    }

    public String getCodigoPostalArgentino() {
        return codigo_postal_argentino;
    }

    public void logData() {
        String mensaje = "id = " + id + " location " + this.getLocation() + " long = " + longitude + " lat = " + lat + " banco = " + banco + " red = "+ red + " ubicacion = " + ubicacion + " localidad = " + localidad +" terminales = " + terminales + " no_vidente = " + no_vidente + " dolares = " + dolares + " calle = " + calle + " altura = " + altura + " calle2 = " + calle2 + " barrio = " + barrio + " comuna = " + comuna + " codigo_postal = " + codigo_postal + " codigo_postal_argentino = " + codigo_postal_argentino;

        Log.println(Log.VERBOSE, "BANKATM", mensaje);
    }
}
