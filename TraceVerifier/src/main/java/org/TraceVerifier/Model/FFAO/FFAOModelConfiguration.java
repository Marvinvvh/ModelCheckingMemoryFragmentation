package org.TraceVerifier.Model.FFAO;

import org.TraceVerifier.Model.AllocatorConfiguration;
import org.TraceVerifier.Model.ModelType;
import org.TraceVerifier.Model.ModelVariables;

import java.text.MessageFormat;

/**
 * FFAO model variables, e.g., for UPPAAL model injection.
 */
public class FFAOModelConfiguration extends AllocatorConfiguration {
    private final String allocatorTag;
    private final String allocatorTemplateName;
    private final ModelType modelType;

    public FFAOModelConfiguration() {
        super("");
        allocatorTag = "FFAO_ALLOCATOR_CONSTANTS";
        allocatorTemplateName = "ffao_allocator";
        modelType = ModelType.FFAO;
    }

    @Override
    protected String generateIdentifier() {
        return "FFAO-" + getSizeMultiple() + "-size-multiple";
    }

    @Override
    public ModelVariables createModelVariables() {
        return new FFAOModelVariables();
    }

    public ModelType getModelType() {
        return modelType;
    }

    public String getAllocatorTemplateName() {
        return allocatorTemplateName;
    }

    @Override
    public String getAllocatorTag() {
        return allocatorTag;
    }

    @Override
    public String generateAllocatorConstants() {
        return "\n"
                + MessageFormat.format("const int FFAO_ALLOCATOR_COUNT = {0,number,#};\n", getAllocatorCount());
    }
}
