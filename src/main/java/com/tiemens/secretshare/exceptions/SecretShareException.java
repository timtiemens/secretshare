/*******************************************************************************
 * Copyright (c) 2009, 2014 Tim Tiemens.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 *
 * Contributors:
 *     Tim Tiemens - initial API and implementation
 *******************************************************************************/
package com.tiemens.secretshare.exceptions;


/**
 * Topmost exception class for secretshare package.
 *
 * @author tiemens
 *
 */
public class SecretShareException
    extends RuntimeException
{
    // ==================================================
    // class static data
    // ==================================================

    private static final long serialVersionUID = 2193317873773384997L;

    // ==================================================
    // class static methods
    // ==================================================



    // ==================================================
    // instance data
    // ==================================================

    // ==================================================
    // factories
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================


    /** Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public SecretShareException(final String message)
    {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public SecretShareException(final String message,
                                final Throwable cause)
    {
        super(message, cause);
    }

    // ==================================================
    // public methods
    // ==================================================

    // ==================================================
    // non public methods
    // ==================================================
}
