package webapp;

import org.json.JSONObject;

public abstract class BasicApp extends WebApp {

	@Override
	protected JSONObject exec(String user, JSONObject request) throws Exception {
		JSONObject response = new JSONObject();
		String method = request.getString("method");

		switch (method) {

		case "exit":
			if (validPassword(request)) exit();
			break;

		case "hello":
			response.put("hello", "world");
			break;

		case "error": // for testing
			if (!validPassword(request)) throw new RuntimeException("something wrong");

		default:
			response = my_exec(method, user, request);
			break;

		}
		return response;
	}

	protected abstract JSONObject my_exec(String method, String user, JSONObject request) throws Exception;

}
