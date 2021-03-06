/*
 * Copyright (c) 2012, 2013 Hemanta Sapkota.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Hemanta Sapkota (laex.pearl@gmail.com)
 */
package com.laex.j2objc;

import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.laex.j2objc.util.LogUtil;

/**
 * The Class CleanupAction.
 */
public class CleanupAction implements IObjectActionDelegate {

    /** The target part. */
    private IWorkbenchPart targetPart;
    
    /** The selected. */
    private Object[] selected;

    /**
     * Instantiates a new cleanup action.
     */
    public CleanupAction() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run(IAction action) {
        if (!action.isEnabled()) {
            return;
        }

        MessageBox mb = new MessageBox(targetPart.getSite().getShell(), SWT.OK | SWT.CANCEL | SWT.ERROR);
        mb.setMessage("This action cleans up all the internal files generated by the plugin. It won't clean up compiled source files. Are you sure you want to proceed ? ");
        int resp = mb.open();

        if (resp == SWT.CANCEL)
            return;

        for (Object o : selected) {
            if (o instanceof IJavaElement) {
                IJavaElement jel = (IJavaElement) o;

                IJavaProject javaProject = jel.getJavaProject();
                AntDelegate antDel = new AntDelegate(javaProject);

                try {
                    antDel.executeCleanup(targetPart.getSite().getShell().getDisplay());
                } catch (IOException e) {
                    LogUtil.logException(e);
                } catch (CoreException e) {
                    LogUtil.logException(e);
                }

                try {
                    javaProject.getResource().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
                } catch (CoreException e) {
                    LogUtil.logException(e);
                }
            }

        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection.isEmpty())
            return;

        IStructuredSelection sel = (IStructuredSelection) selection;
        selected = sel.toArray();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

}
