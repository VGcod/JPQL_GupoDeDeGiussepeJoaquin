package org.example.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "factura_venta_detalle")
public class FacturaVentaDetalle extends EntityId {
    @ManyToOne
    @JoinColumn(nullable = false)
    private FacturaVenta factura;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ListaPrecioArticulo listaPrecioArticulo;

    private String descripcion;

    @Column(nullable = false) private double cantidad;
    @Column(nullable = false) private double precioUnitario;
    private double porcentajeBonificacion;
    private double importeNeto;
    private double importeIva;
    @Column(nullable = false) private double importeSubtotal;

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public FacturaVenta getFactura() {
        return factura;
    }

    public void setFactura(FacturaVenta factura) {
        this.factura = factura;
    }

    public double getImporteIva() {
        return importeIva;
    }

    public void setImporteIva(double importeIva) {
        this.importeIva = importeIva;
    }

    public double getImporteNeto() {
        return importeNeto;
    }

    public void setImporteNeto(double importeNeto) {
        this.importeNeto = importeNeto;
    }

    public double getImporteSubtotal() {
        return importeSubtotal;
    }

    public void setImporteSubtotal(double importeSubtotal) {
        this.importeSubtotal = importeSubtotal;
    }

    public ListaPrecioArticulo getListaPrecioArticulo() {
        return listaPrecioArticulo;
    }

    public void setListaPrecioArticulo(ListaPrecioArticulo listaPrecioArticulo) {
        this.listaPrecioArticulo = listaPrecioArticulo;
    }

    public double getPorcentajeBonificacion() {
        return porcentajeBonificacion;
    }

    public void setPorcentajeBonificacion(double porcentajeBonificacion) {
        this.porcentajeBonificacion = porcentajeBonificacion;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
