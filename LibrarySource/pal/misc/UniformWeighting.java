// UniformWeighting.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)

package pal.misc;
import java.io.*;

/**
 * A uniform weighting for n values (where each weighting is 1/n)
 * @version $Id: UniformWeighting.java,v 1.2 2001/07/13 14:39:13 korbinian Exp $
 *
 * @author Matthew Goode
 */

public class UniformWeighting implements Weighting {
	private double[] weightings_; 
	/**
		* @param The number of weightings
		*/
	public UniformWeighting(int n) {
		this.weightings_ = new double[n];
		for(int i = 0 ; i < n ; i++) {
     	weightings_[i] = 1.0/(double)n;
		}
	}

	public UniformWeighting(UniformWeighting toCopy) {
		weightings_ = pal.misc.Utils.getCopy(toCopy.weightings_);
	}

	public double getWeight(int weightNumber) {
		return weightings_[weightNumber];
	}

	public double[] getWeights() {
		return weightings_;
	}
	/** For people who don't like casting...*/
	public Weighting getWeightingCopy() {
		return new UniformWeighting(this);
	}
	/**
	 * get number of parameters
	 *
	 * @return number of parameters
	 */
	public int getNumParameters() {
		return 0;
	}
	/**
	 * set model parameter
	 *
	 * @param param  parameter value
	 * @param n  parameter number
	 */
	public void setParameter(double param, int n) {
		throw new RuntimeException("Assertion error - Attempting to set paramters");
	}

	/**
	 * get model parameter
	 *
	 * @param n  parameter number
	 *
	 * @return parameter value
	 */
	public double getParameter(int n) {
		throw new RuntimeException("Assertion error - Attempting to get paramters");
	}


	/**
	 * set standard errors for model parameter
	 *
	 * @param paramSE  standard error of parameter value
	 * @param n parameter number
	 */
	public void setParameterSE(double paramSE, int n) {
		throw new RuntimeException("Assertion error - Illegal paramter usage");
	}


	/**
	 * get lower parameter limit
	 *
	 * @param n parameter number
	 *
	 * @return lower bound
	 */
	public double getLowerLimit(int n) {
		throw new RuntimeException("Assertion error - Illegal paramter usage");
	}

	/**
	 * get upper parameter limit
	 *
	 * @param n parameter number
	 *
	 * @return upper bound
	 */
	public double getUpperLimit(int n) {
		throw new RuntimeException("Assertion error - Illegal paramter usage");
	}


	/**
	 * get default value of parameter
	 *
	 * @param n parameter number
	 *
	 * @return default value
	 */
	public double getDefaultValue(int n) {
		throw new RuntimeException("Assertion error - Illegal paramter usage");
	}

	public void report(PrintWriter out) {
		out.print("Uniform Weighting");
	}
}

