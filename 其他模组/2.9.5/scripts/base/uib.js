function m() {
    return module.exports
};
module.exports = {
    atlas: function (string) {
        return Core.atlas.find(string)
    },
    texture: function (region) {
        return new TextureRegion(m().atlas(region));
    },
    image: function (region) {
        return new Image(m().texture(region))
    },
    drawable: function (string) {
        return new TextureRegionDrawable(m().texture(string))
    },
    intervalX: function (table, w) {
        table.table().size(w, 0);
    },
    intervalY: function (table, h) {
        table.row();
        table.table().size(0, h).row();
    },
    imageText: function (table, string, text, w, h) {
        table.table(Cons(o => {
            o.left();
            o.image(m().drawable(string)).size(w || 32, h || 32);
        }));
        table.table(Cons(t => {
            t.left().bottom();
            t.add(text + "").style(Styles.outlineLabel);
            t.pack();
        }));
    },
    addImage: function (table, string, w, h, color) {
        table.image(m().drawable(string), color || Pal.accent).size(w, h);
    },
    goBack: function (front, back) {
        front.hide();
        back.show();
    },
    bundle: function (string, prefix) {
        return Core.bundle.format((prefix ? prefix : "mod") + ".et-" + string)
    },
    showWiki: function (name, description) {
        var log = new BaseDialog(name);
        log.cont.table(cons(table => {
            table.table(Tex.buttonEdge3, cons(t => {
                t.add(name)
            })).left();
            table.row();
            table.table(Tex.pane, cons(t => {
                t.add(description)
            }))
        }));
        log.addCloseButton();
        log.show();
    }
}