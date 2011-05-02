/*
 * Copyright (C) 2010 Roland Tritsch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tritsch.android.chargefinder;

import junit.framework.Assert;

/**
 * <code>CFStation</code> is just a small helper class to describe
 * a charging station.
 *
 * @author <a href="mailto:roland@tritsch.org">Roland Tritsch</a>
 * @version $Id$
 */

public final class CFStation {
    /**
     * The name of the charging station.
     */
    private String name;

    /**
     * x coordinates for the charging station.
     */
    private double x;

    /**
     * y coordinates for the charging station.
     */
    private double y;

    /**
     * Get the <code>Name</code> value.
     *
     * @return a <code>String</code> value
     */
    public String getName() {
        return name;
    }

    /**
     * Set the <code>Name</code> value.
     *
     * @param newName The new Name value.
     */

    public void setName(final String newName) {
	Assert.assertNotNull(newName); Assert.assertFalse(newName.length() == 0);
        this.name = newName;
    }

    /**
     * Get the <code>X</code> value.
     *
     * @return a <code>double</code> value
     */
    public double getX() {
        return x;
    }

    /**
     * Set the <code>X</code> value.
     *
     * @param newX The new X value.
     */
    public void setX(final double newX) {
        this.x = newX;
    }

    /**
     * Get the <code>Y</code> value.
     *
     * @return a <code>double</code> value
     */
    public double getY() {
        return y;
    }

    /**
     * Set the <code>Y</code> value.
     *
     * @param newY The new Y value.
     */
    public void setY(final double newY) {
        this.y = newY;
    }
}
