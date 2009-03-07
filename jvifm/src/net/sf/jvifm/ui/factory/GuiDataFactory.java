/*
 * Jvifm - Java vifm (File Manager with vi like key binding)
 *
 * Copyright (C) 2006 wsn <shrek.wang@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package net.sf.jvifm.ui.factory;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

public class GuiDataFactory {

	public static GridData createGridData(int verticalAlignment,
			int horizontalAlignment, boolean grabExcessHorizontalSpace,
			boolean grabExcessVerticalSpace) {

		GridData gridData = new GridData();
		gridData.verticalAlignment = verticalAlignment;
		gridData.horizontalAlignment = horizontalAlignment;
		gridData.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
		gridData.grabExcessVerticalSpace = grabExcessVerticalSpace;

		return gridData;
	}

	public static GridLayout createkGridLayout(int numColumns,
			int verticalSpacing, int horizontalSpacing, int marginWidth,
			int marginHeight, boolean makeColumnsEqualWidth) {

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns;
		gridLayout.verticalSpacing = verticalSpacing;
		gridLayout.marginWidth = marginWidth;
		gridLayout.marginHeight = marginHeight;
		gridLayout.horizontalSpacing = horizontalSpacing;
		gridLayout.makeColumnsEqualWidth = makeColumnsEqualWidth;

		return gridLayout;
	}
}
