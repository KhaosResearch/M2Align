/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.khaos.m2align.strike;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * @author cristian
 */
public class Contact {

  private String _seq_name;
  private char _chain;

  private int _num_contacts;
  private String _sequence;

  private ArrayList<ArrayList<Integer>> _pairs = new ArrayList<ArrayList<Integer>>();


  public Contact(char chain) {
    _chain = chain;
    _num_contacts = 0;
  }

  public Contact(int seq_length, String pdb_f, char chain) {
    _chain = chain;
    _num_contacts = 0;
    _pairs = new ArrayList<ArrayList<Integer>>(seq_length);
  }

  public final char chain_name() {
    return _chain;
  }

  public final void set_seq(String sequence) {
    _sequence = sequence;
  }

  public final String get_seq_name() {
    return _seq_name;
  }

  public final String get_sequence() {
    return _sequence;
  }

  public final void set_seq_name(String seq_name) {
    _seq_name = seq_name;
  }

  public final int get_seq_length() {
    return _sequence.length();
  }

  public final int num_contacts() {
    return _num_contacts;
  }

  public final ArrayList<ArrayList<Integer>> get_contacts() {
    return _pairs;
  }


  public final void add_pair(int a, int b) {
    _pairs.get(a).add(b);
    ++_num_contacts;
  }

  public final void read_contact_file(String contact_f, int min_distance) {

    FileReader fr = null;
    BufferedReader br = null;

    try {
      fr = new FileReader(contact_f);
      br = new BufferedReader(fr);

      String linea;

      _seq_name = br.readLine();
      _sequence = br.readLine();
      int seq_length = _sequence.length();
      _sequence = _sequence.toUpperCase();

      _pairs.clear();
      _num_contacts = 0;
      int a, b;

      while ((linea = br.readLine()) != null) {

        String[] parts = linea.split(" ");
        _pairs.add(new ArrayList<Integer>());
        a = Integer.parseInt(parts[0]);

        for (int k = 1; k < parts.length; k++) {
          b = Integer.parseInt(parts[k]);
          if (b - a >= min_distance) {
            add_pair(a, b);
          }
        }

      }
    } catch (Exception e) {
      e.printStackTrace();

    } finally {

      try {
        if (null != fr) {
          fr.close();
        }
      } catch (Exception e2) {
        e2.printStackTrace();
      }

    }

  }

  public final void print() {

    System.out.println(_seq_name);
    System.out.println(_sequence);
    int i;
    for (i = 0; i < _sequence.length(); i++) {
      System.out.println(i + " " + _pairs.get(i).toString());
    }
  }

}
