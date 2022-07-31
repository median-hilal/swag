package swag.analysis_graphs.execution_engine.navigations;

import java.io.Serializable;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EncapsulationOfNavigation implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8488517659137176216L;
	private NavigationStep nvStep;

	public NavigationStep getNvStep() {return nvStep;}
	public void setNvStep(NavigationStep nvStep) {this.nvStep = nvStep;}
	
	public EncapsulationOfNavigation(NavigationStep nv) {
		this.nvStep = nv;
	}
	
	@Override
	public boolean equals (Object o){
		
		if(o instanceof EncapsulationOfNavigation){
			EncapsulationOfNavigation env = (EncapsulationOfNavigation) o;
			if (env.getNvStep().getName().equals(this.getNvStep().getName()))
				return true;
		}		
		return false;
	}
	
	@Override
    public int hashCode() {
        return new HashCodeBuilder().
            append(this.nvStep.getName()).            
            toHashCode();
    }		
}
