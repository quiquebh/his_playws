package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.List;
import java.util.Set;

import javax.persistence.*;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Publication {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long id;

    @Lob
    public String title;
    public String language;
    public String docType;
    @Lob
    public String docAbstract;
    public Integer year;
    public Integer volume;
    public Integer startPage;
    public Integer endPage;

    @ManyToMany
    public List<Author> authors;
    @ManyToMany
    public List<Keyword> keywords;
    @ManyToOne
    public Source source;
}
