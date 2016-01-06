package de.braintags.netrelay.mapper;

import java.util.Calendar;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.netrelay.model.Member;

@Entity
public class TestCustomer extends Member {

  public Calendar birthday;
  public String company;
  public String passwordQuestion;
  public String passwordAnswer;
  public String legalForm;
  public String taxNumber;
  public String commercialRegister;
  public String registerOfCraftsmen;
  public Calendar foundationYear;
  public String Street;
  public String StreeNumber;
  public String plz;
  public String city;
  public String country;
  public String telefonDay;
  public String telefonEvening;
  public String contactTimeFrom;
  public String contactTimeTo;
  public String contactRule;
  public boolean contactReleasePost;
  public boolean contactReleaseEmail;
  public boolean contactReleaseTelefon;
  public String financingPreference;
  public String insuranceCompany;
  public String sfClassType;
  public String sfClassValue;
  public String frameworkContactManufacturer;
  public String frameworkNumber;
  public String frameworkDistributor;
  public String ADACStatus;
  public Calendar ADACMemberSince;
  public String ADACCustomerNumber;
  public boolean active;

}