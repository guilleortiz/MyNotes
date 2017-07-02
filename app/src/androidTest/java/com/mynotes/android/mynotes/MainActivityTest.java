package com.mynotes.android.mynotes;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab), isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.NoteTt),
                        withParent(withId(R.id.activity_note))));
        appCompatEditText.perform(scrollTo(), replaceText("nueva nota"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                withId(R.id.Note));
        appCompatEditText2.perform(scrollTo(), replaceText("aaaaaaahre"), closeSoftKeyboard());

        ViewInteraction viewInteraction = onView(
                allOf(withId(R.id.fab_expand_menu_button),
                        withParent(withId(R.id.floatingctionButton)),
                        isDisplayed()));
        viewInteraction.perform(click());

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.accion_save),
                        withParent(withId(R.id.floatingctionButton)),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.my_recycler_view),
                        withParent(withId(R.id.swipeRefreshLayout)),
                        isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.action_attach), isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction viewInteraction2 = onView(
                allOf(withId(R.id.fab_expand_menu_button),
                        withParent(withId(R.id.floatingctionButton)),
                        isDisplayed()));
        viewInteraction2.perform(click());

        ViewInteraction floatingActionButton3 = onView(
                allOf(withId(R.id.accion_save),
                        withParent(withId(R.id.floatingctionButton)),
                        isDisplayed()));
        floatingActionButton3.perform(click());

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.my_recycler_view),
                        withParent(withId(R.id.swipeRefreshLayout)),
                        isDisplayed()));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction viewInteraction3 = onView(
                allOf(withId(R.id.fab_expand_menu_button),
                        withParent(withId(R.id.floatingctionButton)),
                        isDisplayed()));
        viewInteraction3.perform(click());

        ViewInteraction floatingActionButton4 = onView(
                allOf(withId(R.id.accion_edit),
                        withParent(withId(R.id.floatingctionButton)),
                        isDisplayed()));
        floatingActionButton4.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.Note), withText("aaaaaaahre")));
        appCompatEditText3.perform(scrollTo(), replaceText("ahre"), closeSoftKeyboard());

        ViewInteraction floatingActionButton5 = onView(
                allOf(withId(R.id.accion_save),
                        withParent(withId(R.id.floatingctionButton)),
                        isDisplayed()));
        floatingActionButton5.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.itemTitle), withText("nueva nota"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("nueva nota")));

        ViewInteraction recyclerView3 = onView(
                allOf(withId(R.id.my_recycler_view),
                        withParent(withId(R.id.swipeRefreshLayout)),
                        isDisplayed()));
        recyclerView3.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction editText = onView(
                allOf(withId(R.id.NoteTt), withText("nueva nota"),
                        childAtPosition(
                                allOf(withId(R.id.activity_note),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                                0)),
                                0),
                        isDisplayed()));
        editText.check(matches(withText("nueva nota")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
