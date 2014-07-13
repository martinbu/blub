/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.actions;

import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;

import java.util.ArrayList;
import java.util.List;

public class AddItemToUserListActionTest extends AndroidTestCase {

	private static final String TEST_USERLIST_NAME = "testUserList";
	private static final double ITEM_TO_ADD_VALUE = 3.0;
	private static final List<Object> INITIALIZED_LIST_VALUES = new ArrayList<Object>();
	static {
		INITIALIZED_LIST_VALUES.add(1.0);
		INITIALIZED_LIST_VALUES.add(2.0);
	}
	private Sprite testSprite;
	private Project project;
	private UserList userList;

	@Override
	protected void setUp() throws Exception {
		testSprite = new Sprite("testSprite");
		project = new Project(null, "testProject");
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().getCurrentProject().getUserLists().addProjectUserList(TEST_USERLIST_NAME);
		userList = ProjectManager.getInstance().getCurrentProject().getUserLists()
				.getUserList(TEST_USERLIST_NAME, null);
		super.setUp();
	}

	public void testSetVariableWithNumericalFormula() {
		ExtendedActions.setVariable(testSprite, new Formula(ITEM_TO_ADD_VALUE), userList).act(1f);
		assertEquals("Variable not changed", ITEM_TO_ADD_VALUE, userList.getValue());
	}

	public void testSetVariableWithInvalidUserVariable() {
		ExtendedActions.setVariable(testSprite, new Formula(ITEM_TO_ADD_VALUE), null).act(1f);
		assertEquals("Variable changed, but should not!", INITIALIZED_LIST_VALUES, userList.getValue());
	}

	public void testSetVariableWithNumericalStringFormula() {
		String myString = "155";
		ExtendedActions.setVariable(testSprite, new Formula(myString), userList).act(1f);
		assertEquals("String UserVariable not changed!", Double.valueOf(myString), userList.getValue());
	}

	public void testSetVariableWithStringFormula() {
		String myString = "myString";
		ExtendedActions.setVariable(testSprite, new Formula(myString), userList).act(1f);
		assertEquals("String UserVariable not changed!", myString, (String) userList.getValue());
	}

	public void testNullFormula() {
		ExtendedActions.setVariable(testSprite, null, userList).act(1f);
		assertEquals("String UserVariable not changed!", 0d, userList.getValue());
	}

	public void testNotANumberFormula() {
		ExtendedActions.setVariable(testSprite, new Formula(Double.NaN), userList).act(1f);
		assertEquals("String UserVariable not changed!", Double.NaN, userList.getValue());
	}
}