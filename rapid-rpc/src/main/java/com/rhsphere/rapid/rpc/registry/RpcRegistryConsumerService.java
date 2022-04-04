package com.rhsphere.rapid.rpc.registry;

import com.rhsphere.rapid.rpc.client.RpcClient;
import com.rhsphere.rapid.rpc.config.consumer.CachedService;
import com.rhsphere.rapid.rpc.config.consumer.ConsumerConfig;
import com.rhsphere.rapid.rpc.constant.SignConstants;
import com.rhsphere.rapid.rpc.utils.FastJsonConvertUtil;
import com.rhsphere.rapid.rpc.zookeeper.ChangedEvent;
import com.rhsphere.rapid.rpc.zookeeper.NodeListener;
import com.rhsphere.rapid.rpc.zookeeper.ZookeeperClient;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * RpcRegistryConsumerService
 * 服务发现的核心类 监听zookeeper的数据节点发生变更，即时的进行感知
 */
public class RpcRegistryConsumerService extends AbstractRpcRegistry implements NodeListener {

	private final ReentrantLock lock = new ReentrantLock();
	private final ZookeeperClient zookeeperClient;
	/**
	 * interfaceClass:version
	 */
	private final ConcurrentHashMap<String, List<CachedService>> CACHED_SERVICES = new ConcurrentHashMap<>();
	/**
	 * interfaceClass:version
	 */
	private final ConcurrentHashMap<String , ConsumerConfig<?>> CACHED_CONSUMER_CONFIGS = new ConcurrentHashMap<>();

	public RpcRegistryConsumerService(ZookeeperClient zookeeperClient) throws Exception {
		this.zookeeperClient = zookeeperClient;
		//	初始化根节点
		if (!zookeeperClient.checkExists(ROOT_PATH)) {
			zookeeperClient.addPersistentNode(ROOT_PATH, ROOT_VALUE);
		}
		//	传入根节点ROOT_PATH 去监听下一级直接子节点

		/**
		 * 	/rapid-rpc   --->  	rapid-rpc-1.0.0
		 * 		/com.rhsphere.rapid.rpc.invoke.consumer.test.HelloService:1.0.0
		 * 			/providers
		 * 				/192.168.11.101:5678
		 * 				/192.168.11.102:5679
		 * 			/consumers
		 * 				/192.168.11.103
		 *
		 */
		this.zookeeperClient.listener4ChildrenPath(ROOT_PATH, this);
	}

	public ConsumerConfig<?> getConsumer(String interfaceClass, String version) {
		return CACHED_CONSUMER_CONFIGS.get(interfaceClass + SignConstants.COLON + version);
	}

	@Override
	public void nodeChanged(ZookeeperClient client, ChangedEvent event) throws Exception {

		//	节点信息
		String path = event.getPath();
		//	数据信息
		String data = event.getData();
		//	监听类型
		ChangedEvent.Type type = event.getType();

		//	节点新增的代码逻辑：
		if (ChangedEvent.Type.CHILD_ADDED == type) {

			String[] pathArray = null;
			if (!StringUtils.isBlank(path) && (pathArray = path.substring(1).split(SignConstants.DIAGONAL)).length == 2) {
				//	对根节点下的直接子节点进行继续监听，就是我们的服务权限命名+版本号的路径监听
				//	/rapid-rpc/com.rhsphere.rapid.rpc.invoke.consumer.test.HelloService:1.0.0
				this.zookeeperClient.listener4ChildrenPath(path, this);
			}

			//	/rapid-rpc/com.rhsphere.rapid.rpc.invoke.consumer.test.HelloService:1.0.0/providers
			if (!StringUtils.isBlank(path) && (pathArray = path.substring(1).split(SignConstants.DIAGONAL)).length == 3) {
				this.zookeeperClient.listener4ChildrenPath(path, this);
			}
			//	/rapid-rpc/com.rhsphere.rapid.rpc.invoke.consumer.test.HelloService:1.0.0/providers/192.168.11.112
			if (!StringUtils.isBlank(path) && (pathArray = path.substring(1).split(SignConstants.DIAGONAL)).length == 4) {
				try {
					/**
					 * pathArray ===>
					 *
					 * rapid-rpc [0]
					 * com.rhsphere.rapid.rpc.invoke.consumer.test.HelloService:1.0.0  [1]
					 * providers [2]
					 * 192.168.11.112:8080 [3]
					 */
					lock.lock();
					String interfaceClassWithV = pathArray[1];
					String[] arrays = interfaceClassWithV.split(SignConstants.COLON);
					String interfaceClass = arrays[0];
					String version = arrays[1];

					String address = pathArray[3];
					@SuppressWarnings("unchecked")
					Map<String, String> map = FastJsonConvertUtil.convertJSONToObject(data, Map.class);
					int weight = Integer.parseInt(map.get("weight"));
					CachedService cs = new CachedService(address, weight);

					List<CachedService> addresses = CACHED_SERVICES.get(interfaceClass + SignConstants.COLON + version);
					if (addresses == null) {
						//	把数据变更的节点信息加载到缓存
						CopyOnWriteArrayList<CachedService> newAddresses = new CopyOnWriteArrayList<>();
						newAddresses.add(cs);
						CACHED_SERVICES.put(interfaceClassWithV, newAddresses);

						//	创建新的ConsumerConfig对象
						ConsumerConfig<?> consumerConfig = new ConsumerConfig<>();
						consumerConfig.setInterface(interfaceClass);
						CopyOnWriteArrayList<String> urls = new CopyOnWriteArrayList<>();
						for (int i = 0; i < weight; i++) {
							urls.add(address);
						}
						consumerConfig.setUrl(urls);
						//	初始化RpcClient
						consumerConfig.initRpcClient();
						//	继续进行缓存：
						CACHED_CONSUMER_CONFIGS.put(interfaceClass + SignConstants.COLON + version, consumerConfig);

					} else {
						// 	增加一个新的列表
						addresses.add(cs);
						ConsumerConfig<?> consumerConfig = CACHED_CONSUMER_CONFIGS.get(interfaceClassWithV);
						RpcClient rpcClient = consumerConfig.getClient();
						CopyOnWriteArrayList<String> urls = new CopyOnWriteArrayList<>();
						for (CachedService cachedService : addresses) {
							int cWeight = cachedService.getWeight();
							for (int i = 0; i < cWeight; i++) {
								urls.add(cachedService.getAddress());
							}
						}
						//	更新consumerConfig里的urls
						consumerConfig.setUrl(urls);
						//	更新rpcClient里面的urls
						rpcClient.updateConnectedServer(urls);
					}
				} finally {
					lock.unlock();
				}
			}
		}
	}

}
