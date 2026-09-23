package org.example;

import jakarta.persistence.EntityManager;
import org.example.entities.*;

import java.util.Date;
import java.util.List;

public class ConsultasJPQLService {

    private final EntityManager em;

    // El servicio recibe el EntityManager por inyección en el constructor
    public ConsultasJPQLService(EntityManager em) {
        this.em = em;
    }

    // ========================================================================
    // NIVEL 1: Consultas Básicas y Proyecciones
    // ========================================================================

    // 1. Consulta de Entidades Completas
    public List<FacturaVenta> obtenerTodasLasFacturas() {
        return em.createQuery("SELECT f FROM FacturaVenta f", FacturaVenta.class).getResultList();
    }

    // 2. Proyección de Atributos Específicos
    public List<Object[]> obtenerProyeccionFacturas() {
        return em.createQuery("SELECT f.numero, f.fechaEmision, f.importeTotal FROM FacturaVenta f", Object[].class)
                .getResultList();
    }

    // 3. Filtrado por Igualdad (WHERE)
    public List<Articulo> obtenerArticulosPorRubro(String rubroDenominacion) {
        String jpql = "SELECT a FROM Articulo a " +
                "WHERE a.rubro.denominacion = :rubro";
        return em.createQuery(jpql, Articulo.class)
                .setParameter("rubro", rubroDenominacion)
                .getResultList();
    }

    // 4. Filtrado por Rango de Fechas (BETWEEN)
    public List<FacturaVenta> obtenerFacturasPorRangoFechas(Date fechaInicio, Date fechaFin) {
        String jpql = "SELECT f FROM FacturaVenta f " +
                "WHERE f.fechaEmision " +
                "BETWEEN :inicio AND :fin";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("inicio", fechaInicio)
                .setParameter("fin", fechaFin)
                .getResultList();
    }

    // ========================================================================
    // NIVEL 2: Condicionales Combinados, Operadores de Texto y Agregaciones
    // ========================================================================

    // 5. Condicionales Complejos y Verificación de Nulos
    public List<FacturaVenta> obtenerFacturasEmitidasAltoValor() {
        String jpql = "SELECT f FROM FacturaVenta f " +
                "WHERE f.estado = 'EMITIDA' " +
                "AND f.importeTotal > 10000 " +
                "AND f.fechaAnulacion IS NULL";
        return em.createQuery(jpql, FacturaVenta.class).getResultList();
    }

    // 6. Búsqueda por Patrón de Texto (LIKE y LOWER)
    public List<Cliente> buscarClientesPorPatron(String textoBusqueda) {
        String jpql = "SELECT c FROM Cliente c " +
                "WHERE LOWER(c.denominacion) " +
                "LIKE LOWER(:texto) " +
                "OR c.cuitCuil LIKE '20-%'";
        return em.createQuery(jpql, Cliente.class)
                .setParameter("texto", "%" + textoBusqueda + "%")
                .getResultList();
    }

    // 7. Valores Distintos y Ordenamiento (DISTINCT y ORDER BY)
    public List<String> obtenerEstadosUnicos() {
        String jpql = "SELECT DISTINCT f.estado FROM FacturaVenta f ORDER BY f.estado ASC";
        return em.createQuery(jpql, String.class).getResultList();
    }

    // 8. Funciones de Agregación Simples (COUNT, SUM, AVG)
    public Object[] obtenerEstadisticasFacturas() {
        String jpql = "SELECT COUNT(f), SUM(f.importeTotal), AVG(f.importeTotal) FROM FacturaVenta f";
        return em.createQuery(jpql, Object[].class).getSingleResult();
    }

    // 9. Operador de Inclusión (IN)
    public List<PuntoVenta> obtenerPuntosVentaPorNumeros(List<Integer> numerosList) {
        String jpql = "SELECT p FROM PuntoVenta p WHERE p.numero IN :numeros";
        return em.createQuery(jpql, PuntoVenta.class)
                .setParameter("numeros", numerosList)
                .getResultList();
    }

    // ========================================================================
    // NIVEL 3: Navegación de Entidades, JOINs y Subconsultas Simples
    // ========================================================================

    // 10. Navegación Implícita por Relaciones
    public List<FacturaVenta> obtenerFacturasPorUsuarioCarga(String username) {
        String jpql = "SELECT f FROM FacturaVenta f WHERE f.usuarioCarga.usuario = :username";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("username", username)
                .getResultList();
    }

    // 11. Cláusula INNER JOIN Explícita
    public List<FacturaVentaDetalle> obtenerDetallesPorPuntoVenta(int numeroPtoVenta) {
        String jpql = "SELECT d FROM FacturaVenta f JOIN f.detalles d WHERE f.puntoVenta.numero = :ptoVenta";
        return em.createQuery(jpql, FacturaVentaDetalle.class)
                .setParameter("ptoVenta", numeroPtoVenta)
                .getResultList();
    }

    // 12. Cláusula LEFT JOIN (Inclusión de Nulos)
    public List<Object[]> obtenerArticulosConMarcaLeftJoin() {
        String jpql = "SELECT a.denominacion, m.denominacion FROM Articulo a LEFT JOIN a.marca m";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    // 13. Navegación Multinivel con JOINs Combinados
    public List<FacturaVenta> obtenerFacturasPorMarcaArticulo(String marcaDenominacion) {
        String jpql = "SELECT DISTINCT f FROM FacturaVenta f JOIN f.detalles d " +
                "JOIN d.listaPrecioArticulo lpa JOIN lpa.articulo a JOIN a.marca m " +
                "WHERE m.denominacion = :marca";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("marca", marcaDenominacion)
                .getResultList();
    }

    // 14. Subconsulta en Cláusula WHERE
    public List<FacturaVenta> obtenerFacturasMayorAlPromedio() {
        String jpql = "SELECT f FROM FacturaVenta f WHERE f.importeTotal > " +
                "(SELECT AVG(f2.importeTotal) FROM FacturaVenta f2)";
        return em.createQuery(jpql, FacturaVenta.class).getResultList();
    }

    // ========================================================================
    // NIVEL 4: Agrupamiento (GROUP BY) y Filtros de Grupo (HAVING)
    // ========================================================================

    // 15. Agrupamiento Básico (GROUP BY)
    public List<Object[]> obtenerEstadisticasPorPuntoVenta() {
        String jpql = "SELECT f.puntoVenta.descripcion, COUNT(f), SUM(f.importeTotal) " +
                "FROM FacturaVenta f GROUP BY f.puntoVenta.descripcion";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    // 16. Agrupamiento con Condicional de Grupo (HAVING)
    public List<String> obtenerUsuariosConMasDe5Facturas() {
        String jpql = "SELECT f.usuarioCarga.usuario FROM FacturaVenta f " +
                "GROUP BY f.usuarioCarga.usuario HAVING COUNT(f) > 5";
        return em.createQuery(jpql, String.class).getResultList();
    }

    // 17. Agrupamiento y Agregación sobre Entidades Relacionadas
    public List<Object[]> obtenerEstadisticasPorMarca() {
        String jpql = "SELECT m.denominacion, SUM(d.cantidad), SUM(d.importeSubtotal) " +
                "FROM FacturaVentaDetalle d JOIN d.listaPrecioArticulo lpa " +
                "JOIN lpa.articulo a JOIN a.marca m GROUP BY m.denominacion";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    // ========================================================================
    // NIVEL 5: Subconsultas Correlacionadas y Expresiones Condicionales
    // ========================================================================

    // 18. Subconsulta Correlacionada con EXISTS
    public List<Marca> obtenerMarcasConArticulosFacturados() {
        String jpql = "SELECT m FROM Marca m WHERE EXISTS " +
                "(SELECT d FROM FacturaVentaDetalle d JOIN d.listaPrecioArticulo lpa " +
                "JOIN lpa.articulo a WHERE a.marca = m)";
        return em.createQuery(jpql, Marca.class).getResultList();
    }

    // 19. Subconsulta Correlacionada con NOT EXISTS
    public List<Articulo> obtenerArticulosNuncaFacturados() {
        String jpql = "SELECT a FROM Articulo a WHERE NOT EXISTS " +
                "(SELECT d FROM FacturaVentaDetalle d JOIN d.listaPrecioArticulo lpa " +
                "WHERE lpa.articulo = a)";
        return em.createQuery(jpql, Articulo.class).getResultList();
    }

    // 20. Proyección Condicional (CASE WHEN)
    public List<Object[]> clasificarFacturasPorValor() {
        String jpql = "SELECT f.numero, f.importeTotal, " +
                "CASE WHEN f.importeTotal > 50000 THEN 'ALTO VALOR' " +
                "WHEN f.importeTotal BETWEEN 10000 AND 50000 THEN 'MEDIO VALOR' " +
                "ELSE 'BAJO VALOR' END " +
                "FROM FacturaVenta f ORDER BY f.importeTotal DESC";
        return em.createQuery(jpql, Object[].class).getResultList();
    }
}