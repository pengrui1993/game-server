package org.games.jpa;

import jakarta.persistence.*;

@Entity
@Table(name="t_book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public String isbn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}
