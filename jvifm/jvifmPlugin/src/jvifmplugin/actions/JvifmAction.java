package jvifmplugin.actions;

import java.io.PrintWriter;
import java.net.Socket;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jdt.core.IJarEntryResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class JvifmAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	private ISelection selection;

	public JvifmAction() {
	}

	public void run(IAction action) {
		String path=getSelectedFolderPath(selection);
		activeJvifm(path);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	private void activeJvifm(String path) {
		Socket echoSocket = null;
		PrintWriter out = null;

		try {
			echoSocket = new Socket("127.0.0.1", 9999);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			out.println(path);
			out.close();
			echoSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String getSelectedFolderPath(ISelection selection) {

		TreeSelection d;

		if (selection instanceof IStructuredSelection) {
			Object sel = ((IStructuredSelection) selection).getFirstElement();
			if (sel instanceof PlatformObject) {
				PlatformObject p = (PlatformObject) sel;
				IResource resource = (IResource) p.getAdapter(IResource.class);
				if (resource != null)
					return getPath(resource);
			}

			if (sel instanceof IResource) {
				IResource res = (IResource) sel;
				if (res != null)
					return getPath(res);

			}

			if (sel instanceof IJavaElement) {
				IJavaElement je = (IJavaElement) sel;
				try {
					IResource correspondingResource = je
							.getCorrespondingResource();
					if (correspondingResource != null)
						getPath(correspondingResource);
				} catch (JavaModelException ignore) {
				}
			}

			if (sel instanceof IJarEntryResource) {
				IJarEntryResource jar = (IJarEntryResource) sel;
				IPath fullPath = jar.getFullPath();

				return fullPath.makeAbsolute().toOSString();
			}

			if (sel instanceof IAdaptable) {
				IAdaptable ad = (IAdaptable) sel;

				IResource resource = (IResource) ad.getAdapter(IResource.class);
				if (resource != null) {
					return getPath(resource);
				}

			}
			return null;
		}

		return null;
	}

	private String getPath(IResource resource) {
		IPath loc = resource.getLocation();
		if (loc != null) {
			return loc.toOSString();
		}
		return null;
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}