// Logger.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)

package pal.util;

/**
 * Interface for all objects that provide a logging facility. 
 *
 * @author Alexei Drummond
 * @version $Revision: 1.3 $
 */


public interface Logger {
	void log(Object obj);

	void debug(Object obj);

   // boolean isDebugging();
}

