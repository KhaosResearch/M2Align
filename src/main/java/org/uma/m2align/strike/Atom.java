
/**
*   This file is part of STRIKE.
*
*   STRIKE is free software: you can redistribute it and/or modify
*   it under the terms of the GNU Lesser General Public License as published by
*   the Free Software Foundation, either version 3 of the License, or
*   (at your option) any later version.
*
*   STRIKE is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU Lesser General Public License for more details.
*
*   You should have received a copy of the GNU Lesser General Public License
*   along with STRIKE.  If not, see <http://www.gnu.org/licenses/>.
*
*/

package org.uma.m2align.strike;

import java.util.*;

public class Atom {
    
    	private String _chain_ID;
	private int _res_ID;
	private char _amino_accid;
	private String _atom_name = new String(new char[5]);
	private int _atom_serial_number;
	private double _x;
	private double _y;
	private double _z;

        
    /**
     * Constructor to initalize all values.
     * \param chain_ID The chain ID.
     * \param res_ID The residue ID.
     * \param amino_accid The name of the amino accid in one letter code.
     * \param atom_name The name of the atom.
     * \param atom_serial_number The serial number of the atom.
     * \param x The x-coordinate.
     * \param y The y-coordinate.
     * \param z The z-coordinate.
     */
    public Atom(String chain_ID, int res_ID, char amino_accid, String atom_name, int atom_serial_number, double x, double y, double z)
	{
		this._chain_ID = chain_ID;
		this._res_ID = res_ID;
		this._amino_accid = amino_accid;
		this._atom_serial_number = atom_serial_number;
		this._x = x;
		this._y = y;
		this._z = z;
		_atom_name = atom_name;
	}

	//   Methods
	/**
	 * Computes the euclidian distance between two atoms.
	 * \param other_atom The second atom.
	 */
	public final double compute_distance(Atom other_atom)
	{
		return Math.sqrt((_x - other_atom._x) * (_x - other_atom._x) + (_y - other_atom._y) * (_y - other_atom._y) + (_z - other_atom._z) * (_z - other_atom._z));
	}


	/**
	 * Retuns the radius of the atom.
	 * \return The radius of the atom.
	 */
	public final double radius()
	{
		if (_atom_name.charAt(0) == 'O')
		{
			return 1.40;
		}
		else if (_atom_name.charAt(0) == 'N')
		{
			if (_atom_name.charAt(1) == 'Z')
			{
				return 1.5;
			}
			else
			{
				return 1.65;
			}
		}
		else if (_atom_name.charAt(0) == 'C')
		{
			if (_atom_name.charAt(1) == '\0')
			{
				return 1.76;
			}
			else
			{
				if ((_amino_accid == 'R') || (_amino_accid == 'N') || (_amino_accid == 'D') || (_amino_accid == 'E') || (_amino_accid == 'Q') || (_amino_accid == 'H') || (_amino_accid == 'F') || (_amino_accid == 'W') || (_amino_accid == 'Y') || (_amino_accid == 'B') || (_amino_accid == 'Z'))
				{
					if ((_atom_name.charAt(1) == 'A') || (_atom_name.charAt(1) == 'B') || ((_atom_name.charAt(1) == 'G') && ((_amino_accid == 'R') || (_amino_accid == 'E') || (_amino_accid == 'Q'))) || ((_atom_name.charAt(1) == 'D') && (_amino_accid == 'R')))
					{
						return 1.87;
					}
					else
					{
						return 1.76;
					}
				}
				else
				{
					return 1.87;
				}
			}
		}
		else if (_atom_name.charAt(0) == 'S')
		{
			return 1.85;
		}
		else if (_atom_name.charAt(0) == 'A')
		{
			return 1.5;
		}
		else if (_atom_name.charAt(0) == 'E')
		{
			return 1.9;
		}
		else if (_atom_name.charAt(0) == 'H')
		{
			return 1.0;
		}
		else
		{
			return 0;
		}

	}


}
