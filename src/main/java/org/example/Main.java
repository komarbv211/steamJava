package org.example;

import org.example.entities.Genre;

public class Main {

    //postgresql://neondb_owner:npg_2anZdxqHh6eK@ep-weathered-frog-a5h9s5u2-pooler.us-east-2.aws.neon.tech/neondb?sslmode=require


    public static void main(String[] args) {
        var session = HibernateUtil.getSession();
        try {
            session.beginTransaction();

            var genre = new Genre();
            genre.setName("Сталкер 2");
//            session.save(genre);
            session.persist(genre);

            session.getTransaction().commit();
        } catch(Exception ex) {
            System.out.println("Щось пішло не так! "+ ex.getMessage());
        }
    }
}