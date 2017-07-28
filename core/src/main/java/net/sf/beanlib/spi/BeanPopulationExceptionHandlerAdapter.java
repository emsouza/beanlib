/*
 * Copyright 2007 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.beanlib.spi;

import java.lang.reflect.Method;

import org.slf4j.Logger;

import net.sf.beanlib.BeanlibException;

/**
 * Adapter class for {@link BeanPopulationExceptionHandler}.
 *
 * @author Joe D. Velopar
 */
public class BeanPopulationExceptionHandlerAdapter implements BeanPopulationExceptionHandler {

    protected String propertyName;

    protected Object fromBean;

    protected Method readerMethod;

    protected Object toBean;

    protected Method setterMethod;

    @Override
    public void handleException(Throwable t, Logger log) {
        log.error("\n" + "propertyName=" + propertyName + "\n" + "readerMethod=" + readerMethod + "\n" + "setterMethod=" + setterMethod + "\n"
                + "fromBean=" + fromBean + "\n" + "toBean=" + toBean + "\n", t);

        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        }
        if (t instanceof Error) {
            throw (Error) t;
        }
        throw new BeanlibException(t);
    }

    @Override
    public BeanPopulationExceptionHandlerAdapter initPropertyName(String propertyName) {
        this.propertyName = propertyName;
        return this;
    }

    @Override
    public BeanPopulationExceptionHandlerAdapter initFromBean(Object fromBean) {
        this.fromBean = fromBean;
        return this;
    }

    @Override
    public BeanPopulationExceptionHandlerAdapter initReaderMethod(Method readerMethod) {
        this.readerMethod = readerMethod;
        return this;
    }

    @Override
    public BeanPopulationExceptionHandlerAdapter initToBean(Object toBean) {
        this.toBean = toBean;
        return this;
    }

    @Override
    public BeanPopulationExceptionHandlerAdapter initSetterMethod(Method setterMethod) {
        this.setterMethod = setterMethod;
        return this;
    }
}
