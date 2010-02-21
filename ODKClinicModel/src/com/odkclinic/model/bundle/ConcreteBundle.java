/**
 * 
 */

package com.odkclinic.model.bundle;

/**
 * @author zellv
 * 
 */
public class ConcreteBundle<T> extends AbstractBundle<T>
{
    @SuppressWarnings( { "unchecked", "unused" })
    private Class cls;

    @SuppressWarnings("unchecked")
    private ConcreteBundle(Class cls)
    {
        super(cls);
    }

    @SuppressWarnings("unchecked")
    public ConcreteBundle<?> getInstance(Class cls)
    {
        return new ConcreteBundle(cls);
    }
}
