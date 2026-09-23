package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.entities.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            Date fechaActual = new Date();
            Date fechaVtoCae = Date.from(LocalDate.now().plusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant());

            // ==============================================================================
            // 1. USUARIO Y DEPENDENCIAS FISCALES
            // ==============================================================================
            Usuario usuario = new Usuario();
            usuario.setUsuario("cguiretti");
            usuario.setClave("Contraseña123");
            usuario.setNombre("Cristian Valentino");
            usuario.setApellido("Guiretti");
            em.persist(usuario);

            PuntoVenta puntoVenta1 = new PuntoVenta();
            puntoVenta1.setNumero(10);
            puntoVenta1.setDescripcion("Sucursal Principal Mendoza");
            puntoVenta1.setTipoEmision("Facturación Electrónica");
            puntoVenta1.setDomicilioComercial("Av. San Martín 1024");
            setAuditoria(puntoVenta1, usuario, fechaActual);
            em.persist(puntoVenta1);

            // NUEVO: Segundo punto de venta para probar agrupamientos
            PuntoVenta puntoVenta2 = new PuntoVenta();
            puntoVenta2.setNumero(11);
            puntoVenta2.setDescripcion("Sucursal Terminal");
            puntoVenta2.setTipoEmision("Controlador Fiscal");
            puntoVenta2.setDomicilioComercial("Av. Alberdi 50");
            setAuditoria(puntoVenta2, usuario, fechaActual);
            em.persist(puntoVenta2);

            TipoMoneda moneda = new TipoMoneda();
            moneda.setCodigoAfip("PES");
            moneda.setDenominacion("Pesos Argentinos");
            moneda.setSimbolo("$");
            setAuditoria(moneda, usuario, fechaActual);
            em.persist(moneda);

            CondicionIva iva = new CondicionIva();
            iva.setCodigoAfip(1);
            iva.setDenominacion("IVA Responsable Inscripto");
            setAuditoria(iva, usuario, fechaActual);
            em.persist(iva);

            // ==============================================================================
            // 2. CLIENTE
            // ==============================================================================
            Contacto contacto = new Contacto();
            contacto.setEmail("compras@empresa.com.ar");
            contacto.setTelefono("0261-4200000");
            contacto.setCelular("2615551234");
            em.persist(contacto);

            Domicilio domicilio = new Domicilio();
            domicilio.setNombreCalle("Belgrano");
            domicilio.setNumeroCalle("450");
            em.persist(domicilio);

            Cliente cliente = new Cliente();
            cliente.setCuitCuil("20-71234567-1");
            cliente.setDenominacion("Tech Solutions S.A.");
            cliente.setContacto(contacto);
            cliente.setDomicilio(domicilio);
            setAuditoria(cliente, usuario, fechaActual);
            em.persist(cliente);

            // ==============================================================================
            // 3. CATÁLOGO DE ARTÍCULOS (Con y sin facturar)
            // ==============================================================================
            Rubro rubro1 = new Rubro();
            rubro1.setCodigo(100);
            rubro1.setDenominacion("Insumos y Periféricos Informáticos");
            setAuditoria(rubro1, usuario, fechaActual);
            em.persist(rubro1);

            Marca marca1 = new Marca();
            marca1.setCodigo(200);
            marca1.setDenominacion("Logitech G-Series");
            setAuditoria(marca1, usuario, fechaActual);
            em.persist(marca1);

            Articulo articulo1 = new Articulo();
            articulo1.setCodigo("ART-01");
            articulo1.setDenominacion("Mouse Inalámbrico M185");
            articulo1.setRubro(rubro1);
            articulo1.setMarca(marca1);
            setAuditoria(articulo1, usuario, fechaActual);
            em.persist(articulo1);

            ListaPrecio listaPrecio = new ListaPrecio();
            listaPrecio.setCodigo("LP-MAY-26");
            listaPrecio.setDenominacion("Lista Mayorista 2026");
            setAuditoria(listaPrecio, usuario, fechaActual);
            em.persist(listaPrecio);

            ListaPrecioArticulo precioArt1 = new ListaPrecioArticulo();
            precioArt1.setListaPrecio(listaPrecio);
            precioArt1.setArticulo(articulo1);
            precioArt1.setPrecioVenta(15000.0);
            setAuditoria(precioArt1, usuario, fechaActual);
            em.persist(precioArt1);

            // NUEVO: Artículo que nunca será facturado (Para probar NOT EXISTS y LEFT JOIN)
            Rubro rubro2 = new Rubro();
            rubro2.setCodigo(101);
            rubro2.setDenominacion("Almacenamiento");
            setAuditoria(rubro2, usuario, fechaActual);
            em.persist(rubro2);

            Articulo articuloNoFacturado = new Articulo();
            articuloNoFacturado.setCodigo("ART-02");
            articuloNoFacturado.setDenominacion("Disco Sólido SSD 1TB");
            articuloNoFacturado.setRubro(rubro2);
            articuloNoFacturado.setMarca(null); // Sin marca para probar LEFT JOIN
            setAuditoria(articuloNoFacturado, usuario, fechaActual);
            em.persist(articuloNoFacturado);

            // ==============================================================================
            // 4. FACTURACIÓN
            // ==============================================================================
            // FACTURA 1 (Sede Principal)
            FacturaVenta factura1 = new FacturaVenta();
            factura1.setNumero(1001L);
            factura1.setFechaEmision(fechaActual);
            factura1.setEstado("APROBADA");
            factura1.setPuntoVenta(puntoVenta1);
            factura1.setTipoMoneda(moneda);
            factura1.setCondicionIva(iva);
            factura1.setCliente(cliente);
            factura1.setImporteTotal(27000.0);
            setAuditoria(factura1, usuario, fechaActual);

            FacturaVentaDetalle det1 = new FacturaVentaDetalle();
            det1.setListaPrecioArticulo(precioArt1);
            det1.setDescripcion("Mouse Inalámbrico M185");
            det1.setCantidad(2);
            det1.setPrecioUnitario(15000.0);
            det1.setImporteSubtotal(27000.0);
            factura1.addDetalle(det1);
            em.persist(factura1);

            // NUEVO: FACTURA 2 (Sede Terminal, monto menor)
            FacturaVenta factura2 = new FacturaVenta();
            factura2.setNumero(1002L);
            factura2.setFechaEmision(fechaActual);
            factura2.setEstado("APROBADA");
            factura2.setPuntoVenta(puntoVenta2);
            factura2.setTipoMoneda(moneda);
            factura2.setCondicionIva(iva);
            factura2.setCliente(cliente);
            factura2.setImporteTotal(13500.0);
            setAuditoria(factura2, usuario, fechaActual);

            FacturaVentaDetalle det2 = new FacturaVentaDetalle();
            det2.setListaPrecioArticulo(precioArt1);
            det2.setDescripcion("Mouse Inalámbrico M185 - Promo individual");
            det2.setCantidad(1);
            det2.setPrecioUnitario(15000.0);
            det2.setImporteSubtotal(13500.0);
            factura2.addDetalle(det2);
            em.persist(factura2);

            em.getTransaction().commit();
            System.out.println("ÉXITO: Base de datos poblada con variedad de registros.\n");

            // ==============================================================================
            // 5. EJECUCIÓN  DE CONSULTAS JPQL
            // ==============================================================================
            System.out.println("============== RESULTADOS JPQL ==============");
            ConsultasJPQLService srv = new ConsultasJPQLService(em);

            // Nivel 1 - C3: Filtrado por WHERE
            System.out.println("\n[3] Artículos del Rubro 'Insumos y Periféricos Informáticos':");
            srv.obtenerArticulosPorRubro("Insumos y Periféricos Informáticos")
                    .forEach(a -> System.out.println("    - " + a.getDenominacion()));

            // Nivel 2 - C9: Operador IN
            System.out.println("\n[9] Puntos de Venta (Filtro IN 10, 11, 99):");
            srv.obtenerPuntosVentaPorNumeros(Arrays.asList(10, 11, 99))
                    .forEach(pv -> System.out.println("    - PV Nº" + pv.getNumero() + " (" + pv.getDescripcion() + ")"));

            // Nivel 3 - C12: LEFT JOIN (Mostrará el SSD aunque su marca sea null)
            System.out.println("\n[12] Artículos y sus marcas (LEFT JOIN):");
            srv.obtenerArticulosConMarcaLeftJoin().forEach(fila ->
                    System.out.println("    - Artículo: " + fila[0] + " | Marca: " + (fila[1] == null ? "SIN MARCA" : fila[1]))
            );

            // Nivel 4 - C15: Agrupamiento (GROUP BY)
            System.out.println("\n[15] Estadísticas facturadas por Punto de Venta:");
            srv.obtenerEstadisticasPorPuntoVenta().forEach(fila ->
                    System.out.println("    - " + fila[0] + " | Cantidad: " + fila[1] + " | Total Facturado: $" + fila[2])
            );

            // Nivel 5 - C18: EXISTS
            System.out.println("\n[18] Marcas con artículos que YA fueron facturados (EXISTS):");
            srv.obtenerMarcasConArticulosFacturados()
                    .forEach(m -> System.out.println("    - " + m.getDenominacion()));

            // Nivel 5 - C19: NOT EXISTS (Debería mostrar solo el SSD)
            System.out.println("\n[19] Artículos que NUNCA fueron facturados (NOT EXISTS):");
            srv.obtenerArticulosNuncaFacturados()
                    .forEach(a -> System.out.println("    - " + a.getDenominacion() + " (Código: " + a.getCodigo() + ")"));

            // Nivel 5 - C20: CASE WHEN
            System.out.println("\n[20] Clasificación de las facturas (CASE WHEN):");
            srv.clasificarFacturasPorValor().forEach(fila ->
                    System.out.println("    - Factura Nº: " + fila[0] + " | Importe: $" + fila[1] + " | Categoría: " + fila[2])
            );

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    private static void setAuditoria(AuditoriaApp entidad, Usuario usuario, Date fecha) {
        entidad.setFechaAlta(fecha);
        entidad.setFechaModificacion(fecha);
        entidad.setUsuarioCarga(usuario);
        entidad.setUsuarioModificacion(usuario);
    }
}