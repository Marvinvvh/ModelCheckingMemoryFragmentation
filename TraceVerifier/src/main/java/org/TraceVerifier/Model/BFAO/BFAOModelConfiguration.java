package org.TraceVerifier.Model.BFAO;

import com.google.gson.annotations.Expose;
import org.TraceVerifier.Model.AllocatorConfiguration;
import org.TraceVerifier.Model.ModelType;
import org.TraceVerifier.Model.ModelVariables;

import java.text.MessageFormat;

/**
 * BFAO model variables, e.g., for UPPAAL model injection.
 */
public class BFAOModelConfiguration extends AllocatorConfiguration {
    private final String allocatorTag;
    private final String allocatorTemplateName;
    private final ModelType modelType;
    @Expose
    private int tolerance = 1;

    public BFAOModelConfiguration() {
        super("");
        refreshIdentifier();
        this.allocatorTag = "BFAO_ALLOCATOR_CONSTANTS";
        this.allocatorTemplateName = "bfao_allocator";
        this.modelType = ModelType.BFAO;
    }

    @Override
    public ModelVariables createModelVariables() {
        return new BFAOModelVariables();
    }

    @Override
    public String generateAllocatorConstants() {
        return "\n"
                + MessageFormat.format("const int32_t BFAO_ALLOCATOR_COUNT = {0,number,#};\n", getAllocatorCount());
    }

    @Override
    public String getAllocatorTag() {
        return this.allocatorTag;
    }

    @Override
    public String getAllocatorTemplateName() {
        return allocatorTemplateName;
    }

    @Override
    public ModelType getModelType() {
        return modelType;
    }

    @Override
    protected String generateIdentifier() {
        return "BFAO-" + getSizeMultiple() + "-size-multiple";
    }
}
