package org.TraceVerifier.Model.NFAO;

import org.TraceVerifier.Model.AllocatorConfiguration;
import org.TraceVerifier.Model.ModelType;
import org.TraceVerifier.Model.ModelVariables;

import java.text.MessageFormat;

/**
 * NFAO model variables, e.g., for UPPAAL model injection.
 */
public class NFAOModelConfiguration extends AllocatorConfiguration {
    private final String allocatorTag;
    private final String allocatorTemplateName;
    private final ModelType modelType;

    public NFAOModelConfiguration() {
        super("");
        refreshIdentifier();
        allocatorTag = "NFAO_ALLOCATOR_CONSTANTS";
        modelType = ModelType.NFAO;
        allocatorTemplateName = "nfao_allocator";
    }

    @Override
    protected String generateIdentifier() {
        return "NFAO-" + getSizeMultiple() + "-size-multiple";
    }

    @Override
    public String generateAllocatorConstants() {
        return "\n"
                + MessageFormat.format("const int32_t NFAO_ALLOCATOR_COUNT = {0,number,#};\n", getAllocatorCount());
    }

    @Override
    public ModelVariables createModelVariables() {
        return new NFAOModelVariables();
    }

    @Override
    public String getAllocatorTag() {
        return allocatorTag;
    }

    @Override
    public String getAllocatorTemplateName() {
        return allocatorTemplateName;
    }

    @Override
    public ModelType getModelType() {
        return modelType;
    }
}
