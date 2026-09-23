package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.entities.Cliente;
import org.example.entities.Contacto;
import org.example.entities.Domicilio;
import org.example.entities.Usuario;

import java.util.Date;

public class CrearCliente {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // 1. Recuperamos el usuario que ya existe en la base de datos (ID 1)
            Usuario usuarioExistente = em.find(Usuario.class, 3L);
            if (usuarioExistente == null) {
                throw new RuntimeException("No se encontró el usuario con ID 1.");
            }

            // 2. Instanciamos y persistimos Contacto y Domicilio
            Contacto contacto = new Contacto();
            contacto.setEmail("ventas@nuevocliente.com");
            contacto.setTelefono("0261-4333222");
            contacto.setCelular("2616112233");
            em.persist(contacto);

            Domicilio domicilio = new Domicilio();
            domicilio.setNombreCalle("Av. Colón");
            domicilio.setNumeroCalle("580");
            em.persist(domicilio);

            // 3. Instanciamos el nuevo Cliente
            Cliente nuevoCliente = new Cliente();
            nuevoCliente.setCuitCuil("30-88999111-2");
            nuevoCliente.setDenominacion("Distribuidora Cuyana S.R.L.");
            nuevoCliente.setContacto(contacto);
            nuevoCliente.setDomicilio(domicilio);

            // 4. Completamos los campos de auditoría obligatorios con el usuario existente
            Date ahora = new Date();
            nuevoCliente.setFechaAlta(ahora);
            nuevoCliente.setFechaModificacion(ahora);
            nuevoCliente.setUsuarioCarga(usuarioExistente);
            nuevoCliente.setUsuarioModificacion(usuarioExistente);

            // 5. Guardamos exclusivamente el cliente
            em.persist(nuevoCliente);

            em.getTransaction().commit();
            System.out.println("Cliente ID " + nuevoCliente.getId() + " guardado con éxito.");

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}