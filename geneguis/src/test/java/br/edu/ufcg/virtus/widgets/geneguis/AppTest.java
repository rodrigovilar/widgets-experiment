package br.edu.ufcg.virtus.widgets.geneguis;

import org.junit.Test;

import br.edu.ufcg.embedded.ise.geneguis.WidgetType;
import br.edu.ufcg.embedded.ise.geneguis.backend.controller.PortRest;
import br.edu.ufcg.virtus.widgets.geneguis.domain.Component;
import br.edu.ufcg.virtus.widgets.geneguis.domain.ComponentRepository;

public class AppTest extends WebBrowserTestCase {

	@Test
	public void testApp() throws Exception {
		deployEntityType(Component.class, ComponentRepository.class);

		widget("EntityTypeList", WidgetType.EntityTypeSet, new PortRest("entity_type_item", EntityType.name()));
		widget("EntityTypeItem", EntityType, new PortRest("entity_type_page", EntityType.name()));
		widget("EntityTitle", EntityType);

		rule("root", "EntityTypeList", EntityTypeSet);
		rule("entity_type_item", "EntityTypeItem", EntityType);
		rule("entity_type_page", "EntityTitle", EntityType);

		openApp();
		clickEntityType(Component.class);
		checkTitle(Component.class);
	}
}
