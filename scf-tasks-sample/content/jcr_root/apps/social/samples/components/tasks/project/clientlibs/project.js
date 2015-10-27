(function($CQ, _, Backbone, SCF) {"use strict";
	var Project = SCF.Model.extend({
		modelName : "ProjectModel",
		remove : function() {
			var that = this;
			var success = function(response) {
				that.log.debug("removed project!");
				that.trigger('destroy', that);
				that = undefined;
			};
			var error = function(e) {
				that.log.error("Unable to delete project");
			};
			var postData = {};
			postData[":operation"] = "social:samples:deleteProject";
			$CQ.ajax(SCF.config.urlRoot + this.get("id") + SCF.constants.URL_EXT, {
				dataType : "json",
				type : "POST",
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				xhrFields : {
					withCredentials : true
				},
				data : this.addEncoding(postData),
				"success" : success,
				"error" : error
			});
		}
	});

	var ProjectView = SCF.View.extend({
		viewName : "Project",
		init : function() {
			this.listenTo(this.model, "change", this.render);
			this.listenTo(this.model, "destroy", this.destroy);
		},
		requiresSession : true,
		showToolBar : function() {
			this.$el.find(".scf-js-project-tools").show();
		},
		hideToolBar : function() {
			this.$el.find(".scf-js-project-tools").hide();
		},
		deleteProject : function(e) {
			e.preventDefault();
			this.model.remove();
		}
	});

	SCF.registerComponent('social/samples/components/tasks/project', Project, ProjectView);

})($CQ, _, Backbone, SCF);
