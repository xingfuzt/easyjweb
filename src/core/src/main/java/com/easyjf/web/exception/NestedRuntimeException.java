/*
 * Copyright 2006-2008 the original author or authors.
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
package com.easyjf.web.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
/**
 * 
 * @author 大峡
 *
 */
public abstract class NestedRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 56546455435047936L;

	/** Root cause of this nested exception */
	private Throwable cause;


	/**
	 * Construct a <code>NestedRuntimeException</code> with the specified detail message.
	 * @param msg the detail message
	 */
	public NestedRuntimeException(String msg) {
		super(msg);
	}

	/**
	 * Construct a <code>NestedRuntimeException</code> with the specified detail message
	 * and nested exception.
	 * @param msg the detail message
	 * @param cause the nested exception
	 */
	public NestedRuntimeException(String msg, Throwable cause) {
		super(msg);
		this.cause = cause;
	}


	/**
	 * Return the nested cause, or <code>null</code> if none.
	 * <p>Note that this will only check one level of nesting.
	 * Use <code>getRootCause()</code> to retrieve the innermost cause.
	 * @see #getRootCause()
	 */
	public Throwable getCause() {
		// Even if you cannot set the cause of this exception other than through
		// the constructor, we check for the cause being "this" here, as the cause
		// could still be set to "this" via reflection: for example, by a remoting
		// deserializer like Hessian's.
		return (this.cause == this ? null : this.cause);
	}

	/**
	 * Return the detail message, including the message from the nested exception
	 * if there is one.
	 */
	public String getMessage() {
		return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
	}

	/**
	 * Print the composite message and the embedded stack trace to the specified stream.
	 * @param ps the print stream
	 */
	public void printStackTrace(PrintStream ps) {
		if (getCause() == null) {
			super.printStackTrace(ps);
		}
		else {
			ps.println(this);
			ps.print("Caused by: ");
			getCause().printStackTrace(ps);
		}
	}

	/**
	 * Print the composite message and the embedded stack trace to the specified writer.
	 * @param pw the print writer
	 */
	public void printStackTrace(PrintWriter pw) {
		if (getCause() == null) {
			super.printStackTrace(pw);
		}
		else {
			pw.println(this);
			pw.print("Caused by: ");
			getCause().printStackTrace(pw);
		}
	}


	/**
	 * Retrieve the innermost cause of this exception, if any.
	 * <p>Currently just traverses NestedRuntimeException causes. Will use
	 * the JDK 1.4 exception cause mechanism once Spring requires JDK 1.4.
	 * @return the innermost exception, or <code>null</code> if none
	 */
	public Throwable getRootCause() {
		Throwable cause = getCause();
		if (cause instanceof NestedRuntimeException) {
			return ((NestedRuntimeException) cause).getRootCause();
		}
		else {
			return cause;
		}
	}

	/**
	 * Check whether this exception contains an exception of the given class:
	 * either it is of the given class itself or it contains a nested cause
	 * of the given class.
	 * <p>Currently just traverses NestedRuntimeException causes. Will use
	 * the JDK 1.4 exception cause mechanism once Spring requires JDK 1.4.
	 * @param exClass the exception class to look for
	 */
	public boolean contains(Class exClass) {
		if (exClass == null) {
			return false;
		}
		if (exClass.isInstance(this)) {
			return true;
		}
		Throwable cause = getCause();
		if (cause instanceof NestedRuntimeException) {
			return ((NestedRuntimeException) cause).contains(exClass);
		}
		else {
			return (cause != null && exClass.isInstance(cause));
		}
	}

}
