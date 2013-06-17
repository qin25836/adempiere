/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.compiere.process;

import java.util.List;

import org.adempiere.ad.migration.executor.IMigrationExecutor;
import org.adempiere.ad.migration.executor.IMigrationExecutorContext;
import org.adempiere.ad.migration.executor.IMigrationExecutorProvider;
import org.adempiere.util.Services;

public class MigrationApply extends SvrProcess
{
	private int p_AD_Migration_ID = -1;
	private boolean failOnError = true;

	@Override
	protected void prepare()
	{
		p_AD_Migration_ID = getRecord_ID();

		for (ProcessInfoParameter p : getParameter())
		{
			final String name = p.getParameterName();
			if ("FailOnError".equals(name))
			{
				failOnError = p.getParameterAsBoolean();
			}
		}
	}

	@Override
	protected String doIt() throws Exception
	{
		final IMigrationExecutorProvider executorProvider = Services.get(IMigrationExecutorProvider.class);
		final IMigrationExecutorContext context = executorProvider.createContext(getCtx());
		context.setFailOnFirstError(failOnError);
		
		final IMigrationExecutor executor = executorProvider.newMigrationExecutor(context, p_AD_Migration_ID);

		executor.execute();

		final List<Exception> errors = executor.getExecutionErrors();
		for (final Exception error : errors)
		{
			addLog("Error: " + error.getLocalizedMessage());
		}

		return executor.getStatusDescription();
	}
}
