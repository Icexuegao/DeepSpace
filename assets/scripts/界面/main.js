Events.on(ClientLoadEvent, () => {
	Vars.ui.hudGroup.fill(cons(table => {
		let move = new Table(Tex.whiteui.tint(Color.black));
		let [lastx, lasty] = [0, 0];
		move.image(Icon.move.getRegion()).size(45);
		move.addListener(extend(InputListener, {
			touchDown(event, x, y, pointer, button) {
				const v = table.localToParentCoordinates(Tmp.v1.set(x, y));
				[lastx, lasty] = [v.x, v.y];
				return true;
			},
			touchDragged(event, x, y, pointer) {
				const v = table.localToParentCoordinates(Tmp.v1.set(x, y));
				table.translation.add(v.x - lastx, v.y - lasty);
				[lastx, lasty] = [v.x, v.y];
			}
		}));
		
		table.left();
		setup(table);
		table.add(move);
	}));
});

function setup(table) {
	table.defaults().size(60);
	let itemsDialog = new BasicDialog(Vars.content.items(), (t, item) => {
		t.button(Core.atlas.drawable(item.uiIcon), Styles.flati, () => {
			Vars.player.core().items.add(item, java.lang.Integer.MAX_VALUE - Vars.player.core().items.get(item));
		}).size(45);
	});
	let unitsDialog = new BasicDialog(Vars.content.units(), (t, unit) => {
		t.button(Core.atlas.drawable(unit.uiIcon), Styles.flati, 40, () => {
			let pu = Vars.player.unit();
			let u = unit.create(pu.team);
			u.set(pu.x, pu.y);
			u.rotation = pu.rotation;
			u.add();
		}).size(45);
	});
	table.button(Icon.box, Styles.flati, () => itemsDialog.show());
	table.button(Icon.units, Styles.flati, () => unitsDialog.show());
}

function BasicDialog(list, run) {
	let log = new Dialog();
	log.setFillParent(true);
	log.closeOnBack();
	log.buttons.button('@back', Icon.left, Styles.flatt, () => {
		Vars.state.set(GameState.State.playing);
		log.hide();
	}).size(180, 60);
	
	log.cont.pane(table => {
		let i = 0;
		list.each(c => {
			run(table, c);
			if (i % 8 == 7) table.row();
			i++;
		});
		
	});
	
	this.show = () => {
		Vars.state.set(GameState.State.paused);
		log.show();
	}
}