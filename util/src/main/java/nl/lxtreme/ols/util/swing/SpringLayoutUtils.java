/*
 * OpenBench LogicSniffer / SUMP project
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 * 
 * Original copyright message for contained code:
 * 
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.lxtreme.ols.util.swing;


import java.awt.*;

import javax.swing.*;


/**
 * A 1.4 file that provides utility methods for creating form- or grid-style
 * layouts with SpringLayout. These utilities are used by several programs, such
 * as SpringBox and SpringCompactGrid.
 */
public final class SpringLayoutUtils
{
  // CONSTRUCTORS

  /**
   * Creates a new SpringLayoutUtils instance, never used.
   */
  private SpringLayoutUtils()
  {
    // NO-op
  }

  // METHODS

  /**
   * Aligns the first <code>rows</code> * <code>cols</code> components of
   * <code>parent</code> in a grid. Each component in a column is as wide as the
   * maximum preferred width of the components in that column; height is
   * similarly determined for each row. The parent is made just big enough to
   * fit them all.
   * 
   * @param aContainer
   *          the container to layout. Must have a SpringLayout as layout
   *          manager;
   * @param aRows
   *          number of rows
   * @param aCols
   *          number of columns
   * @param aInitialX
   *          x location to start the grid at
   * @param aInitialY
   *          y location to start the grid at
   * @param aXpad
   *          x padding between cells
   * @param aYpad
   *          y padding between cells
   */
  public static void makeCompactGrid( final Container aContainer, final int aRows, final int aCols, final int aInitialX,
      final int aInitialY, final int aXpad, final int aYpad )
  {
    if ( !( aContainer.getLayout() instanceof SpringLayout ) )
    {
      throw new IllegalArgumentException( "Container should have SpringLayout as layout manager!" );
    }

    final SpringLayout layout = ( SpringLayout )aContainer.getLayout();

    // Align all cells in each column and make them the same width.
    Spring x = Spring.constant( aInitialX );
    for ( int c = 0; c < aCols; c++ )
    {
      Spring width = Spring.constant( 0 );
      for ( int r = 0; r < aRows; r++ )
      {
        width = Spring.max( width, getConstraintsForCell( r, c, aContainer, aCols ).getWidth() );
      }
      for ( int r = 0; r < aRows; r++ )
      {
        final SpringLayout.Constraints constraints = getConstraintsForCell( r, c, aContainer, aCols );
        constraints.setX( x );
        constraints.setWidth( width );
      }
      x = Spring.sum( x, Spring.sum( width, Spring.constant( aXpad ) ) );
    }

    // Align all cells in each row and make them the same height.
    Spring y = Spring.constant( aInitialY );
    for ( int r = 0; r < aRows; r++ )
    {
      Spring height = Spring.constant( 0 );
      for ( int c = 0; c < aCols; c++ )
      {
        height = Spring.max( height, getConstraintsForCell( r, c, aContainer, aCols ).getHeight() );
      }
      for ( int c = 0; c < aCols; c++ )
      {
        final SpringLayout.Constraints constraints = getConstraintsForCell( r, c, aContainer, aCols );
        constraints.setY( y );
        constraints.setHeight( height );
      }
      y = Spring.sum( y, Spring.sum( height, Spring.constant( aYpad ) ) );
    }

    // Set the parent's size.
    final SpringLayout.Constraints pCons = layout.getConstraints( aContainer );
    pCons.setConstraint( SpringLayout.SOUTH, y );
    pCons.setConstraint( SpringLayout.EAST, x );
  }

  /**
   * Aligns the first <code>rows</code> * <code>cols</code> components of
   * <code>parent</code> in a grid. Each component is as big as the maximum
   * preferred width and height of the components. The parent is made just big
   * enough to fit them all.
   * 
   * @param aContainer
   *          the container to layout. Must have a SpringLayout as layout
   *          manager;
   * @param aRows
   *          number of rows
   * @param aCols
   *          number of columns
   * @param aInitialX
   *          x location to start the grid at
   * @param aInitialY
   *          y location to start the grid at
   * @param aXpad
   *          x padding between cells
   * @param aYpad
   *          y padding between cells
   */
  public static void makeGrid( final Container aContainer, final int aRows, final int aCols, final int aInitialX,
      final int aInitialY, final int aXpad, final int aYpad )
  {
    if ( !( aContainer.getLayout() instanceof SpringLayout ) )
    {
      throw new IllegalArgumentException( "Container should have SpringLayout as layout manager!" );
    }

    final SpringLayout layout = ( SpringLayout )aContainer.getLayout();

    final Spring xPadSpring = Spring.constant( aXpad );
    final Spring yPadSpring = Spring.constant( aYpad );
    final Spring initialXSpring = Spring.constant( aInitialX );
    final Spring initialYSpring = Spring.constant( aInitialY );
    final int max = aRows * aCols;

    // Calculate Springs that are the max of the width/height so that all
    // cells have the same size.
    Spring maxWidthSpring = layout.getConstraints( aContainer.getComponent( 0 ) ).getWidth();
    Spring maxHeightSpring = layout.getConstraints( aContainer.getComponent( 0 ) ).getWidth();
    for ( int i = 1; i < max; i++ )
    {
      final SpringLayout.Constraints cons = layout.getConstraints( aContainer.getComponent( i ) );

      maxWidthSpring = Spring.max( maxWidthSpring, cons.getWidth() );
      maxHeightSpring = Spring.max( maxHeightSpring, cons.getHeight() );
    }

    // Apply the new width/height Spring. This forces all the
    // components to have the same size.
    for ( int i = 0; i < max; i++ )
    {
      final SpringLayout.Constraints cons = layout.getConstraints( aContainer.getComponent( i ) );

      cons.setWidth( maxWidthSpring );
      cons.setHeight( maxHeightSpring );
    }

    // Then adjust the x/y constraints of all the cells so that they
    // are aligned in a grid.
    SpringLayout.Constraints lastCons = null;
    SpringLayout.Constraints lastRowCons = null;
    for ( int i = 0; i < max; i++ )
    {
      final SpringLayout.Constraints cons = layout.getConstraints( aContainer.getComponent( i ) );
      if ( i % aCols == 0 )
      { // start of new row
        lastRowCons = lastCons;
        cons.setX( initialXSpring );
      }
      else
      { // x position depends on previous component
        cons.setX( Spring.sum( lastCons.getConstraint( SpringLayout.EAST ), xPadSpring ) );
      }

      if ( i / aCols == 0 )
      { // first row
        cons.setY( initialYSpring );
      }
      else
      { // y position depends on previous row
        cons.setY( Spring.sum( lastRowCons.getConstraint( SpringLayout.SOUTH ), yPadSpring ) );
      }
      lastCons = cons;
    }

    // Set the parent's size.
    final SpringLayout.Constraints pCons = layout.getConstraints( aContainer );
    pCons.setConstraint( SpringLayout.SOUTH, Spring.sum( Spring.constant( aYpad ), lastCons
        .getConstraint( SpringLayout.SOUTH ) ) );
    pCons.setConstraint( SpringLayout.EAST, Spring.sum( Spring.constant( aXpad ), lastCons
        .getConstraint( SpringLayout.EAST ) ) );
  }

  /**
   * Used by makeCompactGrid.
   * 
   * @param aRow
   * @param aCol
   * @param aContainer
   * @param aCols
   * @return
   */
  private static SpringLayout.Constraints getConstraintsForCell( final int aRow, final int aCol, final Container aContainer,
      final int aCols )
  {
    final SpringLayout layout = ( SpringLayout )aContainer.getLayout();
    final Component c = aContainer.getComponent( aRow * aCols + aCol );
    return layout.getConstraints( c );
  }

}
