package orbit.gui;

import test.BasicTestCalls;
import test.TestCall;

import junit.framework.*;

public class TestRootRenderState
	extends TestCase
{
	public TestRootRenderState(String name)
	{
		super(name);
	}

	public void testRootRenderState()
	{
		BasicTestCalls calls = new BasicTestCalls();

		class RootClient
			implements RootRenderState.Client
		{
			boolean invalid = false;

			public void invalidate() { invalid = true; }
		}

		RootClient client = new RootClient();
		RootRenderState state = new RootRenderState(client, calls.root);

		assertEquals(state.getRenderCall(), calls.root);
		assert(!state.hasNextRenderCall());
		assert(!state.hasPreviousRenderCall());

		// Test parentRenderCall if there is no parent
		assert(!state.hasParentRenderCall());
		state.setRenderCallToParent();
		assertEquals(state.getRenderCall(), calls.root);
		assert(!state.hasNextRenderCall());
		assert(!state.hasPreviousRenderCall());

		// Navigate down to the call 'root.main'
		// previousRenderCall and setRenderCallToParent should both work now
		state.setRenderCall(calls.main);
		assertEquals(state.getRenderCall(), calls.main);
		assert(!state.hasNextRenderCall());
		assert(state.hasParentRenderCall());
		assert(state.hasPreviousRenderCall());

		// Move up to the parent
		state.setRenderCallToParent();
		assertEquals(state.getRenderCall(), calls.root);
		assert(!state.hasNextRenderCall());
		assert(state.hasPreviousRenderCall());
		assert(!state.hasParentRenderCall());
		
		// Move to the previous call (main)
		state.previousRenderCall();
		assertEquals(state.getRenderCall(), calls.main);
		assert(state.hasNextRenderCall());
		assert(state.hasParentRenderCall());
		assert(state.hasPreviousRenderCall());

		// Move to the next call (root)
		state.nextRenderCall();
		assert(!state.hasNextRenderCall());
		assert(!state.hasParentRenderCall());
		assert(state.hasPreviousRenderCall());

		// Move back to the previous, then set the call state to dbOpen
		// Should not be a 'next' any more
		// Parent should be 'main'
		state.previousRenderCall();
		state.setRenderCall(calls.dbOpen);
		assertEquals(state.getRenderCall(), calls.dbOpen);
		assert(!state.hasNextRenderCall());
		assert(state.hasParentRenderCall());
		assert(state.hasPreviousRenderCall());

		// Underflow the previousRenderCall
		state.previousRenderCall();
		state.previousRenderCall();
		state.previousRenderCall();
		state.previousRenderCall();
		assertEquals(state.getRenderCall(), calls.root);
		
		// Overflow the nextRenderCall
		state.nextRenderCall();
		state.nextRenderCall();
		state.nextRenderCall();
		state.nextRenderCall();
		assertEquals(state.getRenderCall(), calls.dbOpen);
	}
}
