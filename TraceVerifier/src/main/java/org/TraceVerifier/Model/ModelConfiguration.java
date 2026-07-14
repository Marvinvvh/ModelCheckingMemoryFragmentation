package org.TraceVerifier.Model;

/**
 * Interface for retrieving model config information.
 */
public interface ModelConfiguration extends SharedModelSettings {
    public ModelVariables createModelVariables();

    public String getIdentifier();

    public String generateGlobalConstants();

    public String getAllocatorTag();

    public String generateAllocatorConstants();

    public String getAllocatorTemplateName();

    public ModelType getModelType();

}
