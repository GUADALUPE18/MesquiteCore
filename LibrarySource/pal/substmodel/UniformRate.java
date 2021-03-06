// UniformRate.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)


package pal.substmodel;

import pal.misc.*;

import java.io.*;


/**
 * uniform rate distribution
 *
 * @version $Id: UniformRate.java,v 1.4 2001/07/13 14:39:13 korbinian Exp $
 *
 * @author Korbinian Strimmer
 */
public class UniformRate extends RateDistribution implements Serializable
{
	//
	// Public stuff
	//

	/**
	 * construct uniform rate distribution
	 */
	public UniformRate()
	{
		super(1);
		
		rate[0] = 1.0;
		probability[0] = 1.0;
	}
	
	// interface Report
	
	public void report(PrintWriter out)
	{
		out.println("Model of rate heterogeneity: Uniform rate at all sites");
	}

	// interface Parameterized
	
	public int getNumParameters()
	{
		return 0;
	}
	
	public void setParameter(double param, int n)
	{
		return;
	}

	public double getParameter(int n)
	{
		return 0.0;
	}

	public void setParameterSE(double paramSE, int n)
	{
		return;
	}

	public double getLowerLimit(int n)
	{
		return 0.0;
	}
	
	public double getUpperLimit(int n)
	{
		return 0.0;
	}
	
	public double getDefaultValue(int n)
	{
		return 0.0;
	}
}

