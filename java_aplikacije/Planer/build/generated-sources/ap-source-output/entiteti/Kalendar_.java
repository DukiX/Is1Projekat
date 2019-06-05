package entiteti;

import entiteti.Alarmi;
import java.sql.Date;
import java.sql.Time;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2019-05-21T11:18:19")
@StaticMetamodel(Kalendar.class)
public class Kalendar_ { 

    public static volatile SingularAttribute<Kalendar, Date> datum;
    public static volatile SingularAttribute<Kalendar, Time> vreme;
    public static volatile SingularAttribute<Kalendar, Boolean> podsetnik;
    public static volatile SingularAttribute<Kalendar, Alarmi> alarm;
    public static volatile SingularAttribute<Kalendar, String> destinacija;
    public static volatile SingularAttribute<Kalendar, Long> id;
    public static volatile SingularAttribute<Kalendar, String> opis;

}