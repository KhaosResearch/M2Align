
package org.uma.m2align.strike;

import java.util.ArrayList;
import java.util.TreeMap;

public class PDB_chain {

	public char _id;
	public String _seq_res_string;
	public String _atom_res_string;
	public ArrayList<TreeMap< String, Atom>> _atom_list = new ArrayList<TreeMap< String, Atom>>();


	public PDB_chain(char id)
	{
		this._id = id;
		_seq_res_string="";
		_atom_res_string="";
	}

	public final void push_back_seq_res(String amino_accid)
	{
		_seq_res_string = _seq_res_string + amino_accid;
	}

	public final void push_back_atom_res(String amino_accid)
	{
		_atom_res_string = _atom_res_string + amino_accid;
	}
}
