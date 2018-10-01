var vm = new Vue({
	el:'#rrapp',
	data:
	{
		dto:{}
	},
	methods: 
	{
		getDash: function()
		{
			$.getJSON(baseURL + "index/dashboard", function(r)
			{
				if(r.data)
				{
					vm.dto = r.data;
				}
			});
		}
	},
	mounted: function()
	{
		this.getDash();
	}
});
